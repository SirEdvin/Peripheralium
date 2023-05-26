// SPDX-FileCopyrightText: 2022 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0
//
// Another copy :)
package site.siredvin.testiralium.util

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class ItemStackMatcher(private val stack: ItemStack) : TypeSafeMatcher<ItemStack>() {
    companion object {
        fun isStack(stack: ItemStack): Matcher<ItemStack> {
            return ItemStackMatcher(stack)
        }

        fun isStack(item: Item, size: Int): Matcher<ItemStack> {
            return ItemStackMatcher(ItemStack(item, size))
        }
    }
    override fun describeTo(description: Description) {
        description.appendValue(stack).appendValue(stack.tag)
    }

    override fun matchesSafely(item: ItemStack): Boolean {
        return ItemStack.isSameItemSameTags(item, stack) && item.count == stack.count
    }
}
