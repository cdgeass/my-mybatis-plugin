package io.github.cdgeass.inspection

import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * @author cdgeass
 * @since 2021-07-19
 */
class InvalidBoundStatementTest : BasePlatformTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/testData/invalidBoundStatement"
    }

    fun testInspection() {
        val localInspectionTool = InvalidBoundStatementInspection()
        myFixture.enableInspections(localInspectionTool)
        val allQuickFixes = myFixture.getAllQuickFixes("InvalidBoundStatement.java", "InvalidBoundStatement.xml")
        assertNotEmpty(allQuickFixes)
        assertTrue(allQuickFixes.any { it.familyName == "Add statement in InvalidBoundStatement.xml" })
    }
}