package wu.seal.jsontokotlin

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import wu.seal.jsontokotlin.interceptor.InterceptorManager
import wu.seal.jsontokotlin.utils.ClassImportDeclaration
import wu.seal.jsontokotlin.utils.executeCouldRollBackAction

/**
 * to be a helper to insert Import class declare code
 * Created by Seal.Wu on 2017/9/18.
 */


object ClassImportDeclarationWriter : IClassImportDeclarationWriter {


    override fun insertImportClassCode(project: Project?, editFile: Document) {

        val text = editFile.text

        val interceptedImportClassDeclaration = ClassImportDeclaration.applyImportClassDeclarationInterceptors(
                InterceptorManager.getEnabledImportClassDeclarationInterceptors()
        )

        interceptedImportClassDeclaration.split("\n").forEach { importClassLineString ->

            if (importClassLineString !in text) {

                val packageIndex = try {
                    "^[\\s]*package\\s.+\n$".toRegex(RegexOption.MULTILINE).find(text)!!.range.endInclusive
                } catch (e: Exception) {
                    -1
                }
                val lastImportKeywordIndex = try {
                    "^[\\s]*import\\s.+\n$".toRegex(RegexOption.MULTILINE).findAll(text).last().range.endInclusive
                } catch (e: Exception) {
                    -1
                }
                val index = Math.max(lastImportKeywordIndex, packageIndex)
                val insertIndex =
                        if (index == -1) 0 else editFile.getLineEndOffset(editFile.getLineNumber(index))

                executeCouldRollBackAction(project) {
                    editFile.insertString(insertIndex, "\n" + importClassLineString + "\n")
                }

            }
        }
    }

}


