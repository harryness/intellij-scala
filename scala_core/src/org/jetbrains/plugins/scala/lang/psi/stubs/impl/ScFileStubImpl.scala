package org.jetbrains.plugins.scala.lang.psi.stubs.impl
import parser.ScalaElementTypes
import elements.wrappers.PsiFileStubWrapperImpl
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.util.io.StringRef
import com.intellij.psi.stubs.PsiFileStubImpl

/**
 * @author ilyas
 */

class ScFileStubImpl(file: ScalaFile) extends PsiFileStubWrapperImpl[ScalaFile](file) with ScFileStub {

  override def getType = ScalaElementTypes.FILE.asInstanceOf[IStubFileElementType[Nothing]]

  implicit  def refToStr(ref: StringRef) = StringRef.toString(ref)

  var packName: StringRef = _
  var name: StringRef = _

  def this(file: ScalaFile, pName : StringRef, name: StringRef) = {
    this(file)
    this.name = name
    packName = pName
  }

  def getName = name

  def packageName = packageName 

}