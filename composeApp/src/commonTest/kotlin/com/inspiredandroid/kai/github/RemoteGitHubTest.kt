package com.inspiredandroid.kai.github

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RemoteGitHubTest {
    @Test
    fun repositoryNamesRequireExactlyOwnerAndRepo() {
        assertEquals("dude555afk" to "Kairon", splitRepository("dude555afk/Kairon"))
        listOf("", "foo", "foo/bar/extra", "../repo", "foo/..", "foo/bar?ref=main", "foo/bar#fragment").forEach {
            assertFailsWith<IllegalArgumentException> { splitRepository(it) }
        }
    }

    @Test
    fun pathEncodingDoesNotTreatUserInputAsUrlStructure() {
        assertEquals("feature%2Fui-overhaul", encodePathSegment("feature/ui-overhaul"))
        assertEquals("a%20b%3Fref%3Dmain", encodePathSegment("a b?ref=main"))
        assertEquals("%E2%9C%93", encodePathSegment("✓"))
    }
}
