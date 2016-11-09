package com.company;



import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jdt.core.dom.*;


public class Main {


    /**
     * @param sourceFilePath
     */
    public void processJavaFile(String sourceFilePath, Class interfaze){
        try {
            String path = FileUtils.getUserDirectoryPath()+"/Documents/java-pareser/src/com/company";
            System.out.println(sourceFilePath);
            File sourceFile = new File(path+sourceFilePath);
            Document document = createDocumentForTheSourceFile(sourceFile);
            CompilationUnit compilationUnit = createCompilationUnit(document);
            AST abstractSyntaxTree = compilationUnit.getAST();
            boolean addedNoCoverageAnnotation = addNoCoverageAnnotation(compilationUnit, abstractSyntaxTree);
            updateJavaSource(sourceFile, document, compilationUnit, abstractSyntaxTree, addedNoCoverageAnnotation);
        } catch(Exception e) {
            System.out.println("Error "+e);
        }
    }

    private boolean addNoCoverageAnnotation(CompilationUnit unit, AST ast) {
        boolean addedNoCoverageAnnotation = false;
        // to iterate through methods
        List<AbstractTypeDeclaration> types = unit.types();
        for (AbstractTypeDeclaration type : types) {
            if (type.getNodeType() == ASTNode.TYPE_DECLARATION) {
                List<BodyDeclaration> bodies = type.bodyDeclarations();
                for (BodyDeclaration body : bodies) {
                    if (body.getNodeType() == ASTNode.METHOD_DECLARATION) {
                        if(!hasMappingAnnotation(body)) {
                            MarkerAnnotation noCoverage = ast.newMarkerAnnotation();
                            noCoverage.setTypeName(ast.newSimpleName("NoCoverage"));
                            if(body.modifiers().size() > 0 ) {
                                body.modifiers().add(0, noCoverage);
                            } else {
                                body.modifiers().add(noCoverage);
                            }
                            addedNoCoverageAnnotation = true;
                        }
                    }
                }
            }
        }
        return addedNoCoverageAnnotation;
    }

    private Document createDocumentForTheSourceFile(File javaSourceFile) throws IOException {
        String source = FileUtils.readFileToString(javaSourceFile, "UTF-8");
        return new Document(source);
    }

    private CompilationUnit createCompilationUnit(Document document) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(document.get().toCharArray());
        return (CompilationUnit) parser.createAST(null);
    }

    private void updateJavaSource(File sourceFile,
                                  Document document,
                                  CompilationUnit unit, // to create a new import
                                  AST ast,
                                  boolean addedNoCoverageAnnotation,
                                  ) throws BadLocationException, IOException {
        if(addedNoCoverageAnnotation) {
            unit.recordModifications();
            ImportDeclaration id = ast.newImportDeclaration();
            String classToImport = "com.company.type.NoCoverage";
            id.setName(ast.newName(classToImport.split("\\.")));
            unit.imports().add(id);
            // to save the changed file
            TextEdit edits = unit.rewrite(document, null);
            edits.apply(document);
            FileUtils.writeStringToFile(sourceFile, document.get(), "UTF-8");
        }
    }

    private boolean hasMappingAnnotation(BodyDeclaration body) {
        boolean containsMappingAnnotation = false;
        for (Object modifier : body.modifiers()) {
            if (modifier instanceof MarkerAnnotation) {
                MarkerAnnotation annotation = ((MarkerAnnotation) modifier);
                if (annotation.getTypeName().getFullyQualifiedName().equals("Deprecated")) { //should be replaced by Mapping in actual implementation
                    containsMappingAnnotation = true;
                }
            }
        }
        return containsMappingAnnotation;
    }
    /**
     * @param args
     */
    public static void main(String[] args){
        Main javaparser = new Main();

        javaparser.processJavaFile("/IJavaReadable.java", interfaze);
    }
}
