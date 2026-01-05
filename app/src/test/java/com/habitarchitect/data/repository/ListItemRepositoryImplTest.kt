package com.habitarchitect.data.repository

import app.cash.turbine.test
import com.habitarchitect.data.local.database.dao.ListItemDao
import com.habitarchitect.data.local.database.entity.ListItemEntity
import com.habitarchitect.domain.model.ListItem
import com.habitarchitect.domain.model.ListItemType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ListItemRepositoryImplTest {

    private lateinit var listItemDao: ListItemDao
    private lateinit var repository: ListItemRepositoryImpl

    private val testHabitId = "habit-123"
    private val testItemId = "item-123"

    @Before
    fun setup() {
        listItemDao = mockk(relaxed = true)
        repository = ListItemRepositoryImpl(listItemDao)
    }

    @Test
    fun `getListItemsForHabit returns mapped items`() = runTest {
        val entity = createTestListItemEntity()
        every { listItemDao.getListItemsForHabit(testHabitId) } returns flowOf(listOf(entity))

        repository.getListItemsForHabit(testHabitId).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(testItemId, items[0].id)
            assertEquals("Test reason", items[0].content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getListItemsByType filters by type`() = runTest {
        val entity = createTestListItemEntity()
        every {
            listItemDao.getListItemsByType(testHabitId, "RESISTANCE")
        } returns flowOf(listOf(entity))

        repository.getListItemsByType(testHabitId, ListItemType.RESISTANCE).test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(ListItemType.RESISTANCE, items[0].type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `addListItem assigns next order index`() = runTest {
        val entitySlot = slot<ListItemEntity>()
        coEvery { listItemDao.getMaxOrderIndex(testHabitId, "RESISTANCE") } returns 2
        coEvery { listItemDao.insertListItem(capture(entitySlot)) } returns Unit

        val item = createTestListItem()
        val result = repository.addListItem(item)

        assertTrue(result.isSuccess)
        assertEquals(3, entitySlot.captured.orderIndex)
    }

    @Test
    fun `addListItem uses 0 when no existing items`() = runTest {
        val entitySlot = slot<ListItemEntity>()
        coEvery { listItemDao.getMaxOrderIndex(testHabitId, "RESISTANCE") } returns null
        coEvery { listItemDao.insertListItem(capture(entitySlot)) } returns Unit

        val item = createTestListItem()
        val result = repository.addListItem(item)

        assertTrue(result.isSuccess)
        assertEquals(0, entitySlot.captured.orderIndex)
    }

    @Test
    fun `addListItems inserts multiple items`() = runTest {
        val items = listOf(
            createTestListItem().copy(id = "item-1"),
            createTestListItem().copy(id = "item-2")
        )
        coEvery { listItemDao.insertListItems(any()) } returns Unit

        val result = repository.addListItems(items)

        assertTrue(result.isSuccess)
        coVerify { listItemDao.insertListItems(any()) }
    }

    @Test
    fun `updateListItem updates in dao`() = runTest {
        val item = createTestListItem()
        coEvery { listItemDao.updateListItem(any()) } returns Unit

        val result = repository.updateListItem(item)

        assertTrue(result.isSuccess)
        coVerify { listItemDao.updateListItem(any()) }
    }

    @Test
    fun `deleteListItem deletes by id`() = runTest {
        coEvery { listItemDao.deleteListItemById(testItemId) } returns Unit

        val result = repository.deleteListItem(testItemId)

        assertTrue(result.isSuccess)
        coVerify { listItemDao.deleteListItemById(testItemId) }
    }

    @Test
    fun `deleteAllForHabit deletes all items for habit`() = runTest {
        coEvery { listItemDao.deleteAllForHabit(testHabitId) } returns Unit

        val result = repository.deleteAllForHabit(testHabitId)

        assertTrue(result.isSuccess)
        coVerify { listItemDao.deleteAllForHabit(testHabitId) }
    }

    @Test
    fun `reorderItems updates order indices`() = runTest {
        val entities = listOf(
            createTestListItemEntity().copy(id = "item-1", orderIndex = 0),
            createTestListItemEntity().copy(id = "item-2", orderIndex = 1),
            createTestListItemEntity().copy(id = "item-3", orderIndex = 2)
        )
        coEvery { listItemDao.getListItemsByTypeOnce(testHabitId, "RESISTANCE") } returns entities
        coEvery { listItemDao.updateListItem(any()) } returns Unit

        val newOrder = listOf("item-3", "item-1", "item-2")
        val result = repository.reorderItems(testHabitId, ListItemType.RESISTANCE, newOrder)

        assertTrue(result.isSuccess)
        coVerify(exactly = 3) { listItemDao.updateListItem(any()) }
    }

    @Test
    fun `getListItemsByTypeOnce returns items synchronously`() = runTest {
        val entities = listOf(createTestListItemEntity())
        coEvery { listItemDao.getListItemsByTypeOnce(testHabitId, "RESISTANCE") } returns entities

        val result = repository.getListItemsByTypeOnce(testHabitId, ListItemType.RESISTANCE)

        assertEquals(1, result.size)
        assertEquals(testItemId, result[0].id)
    }

    private fun createTestListItemEntity() = ListItemEntity(
        id = testItemId,
        habitId = testHabitId,
        type = "RESISTANCE",
        content = "Test reason",
        orderIndex = 0,
        isFromTemplate = false,
        createdAt = System.currentTimeMillis()
    )

    private fun createTestListItem() = ListItem(
        id = testItemId,
        habitId = testHabitId,
        type = ListItemType.RESISTANCE,
        content = "Test reason",
        orderIndex = 0,
        isFromTemplate = false,
        createdAt = System.currentTimeMillis()
    )
}
