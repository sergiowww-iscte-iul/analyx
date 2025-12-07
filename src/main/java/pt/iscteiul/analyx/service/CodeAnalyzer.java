package pt.iscteiul.analyx.service;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import pt.iscteiul.analyx.model.ArtifactType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeAnalyzer {
    public static class MetricResult {
        public ArtifactType type;
        public String packageName;
        public String className;
        public String methodName;
        public Integer loc;
        public Integer numMethods;
        public Integer numAttributes;
        public Integer cyclomaticComplexity;
        public Integer cbo;
        public Integer dit;
        public Integer noc;
    }

    public List<MetricResult> analyzeJavaFile(Path javaFilePath) throws IOException {
        List<MetricResult> results = new ArrayList<>();

        // Ler conteúdo do arquivo
        String source = Files.readString(javaFilePath);

        // Criar parser AST
        ASTParser parser = ASTParser.newParser(AST.JLS21); // Java 21
        parser.setSource(source.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_21);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_21);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_21);
        parser.setCompilerOptions(options);

        CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        // Visitor para percorrer a AST
        cu.accept(new ASTVisitor() {

            String currentPackage = "";

            @Override
            public boolean visit(PackageDeclaration node) {
                currentPackage = node.getName().getFullyQualifiedName();
                return super.visit(node);
            }

            @Override
            public boolean visit(TypeDeclaration node) {
                if (node.isInterface()) {
                    return true; // Ignorar interfaces por enquanto
                }

                MetricResult classMetrics = new MetricResult();
                classMetrics.type = ArtifactType.CLASS;
                classMetrics.packageName = currentPackage;
                classMetrics.className = node.getName().getIdentifier();

                // LOC da classe (aproximado)
                int startLine = cu.getLineNumber(node.getStartPosition());
                int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
                classMetrics.loc = endLine - startLine + 1;

                // Contar atributos (fields)
                classMetrics.numAttributes = node.getFields().length;

                // Contar métodos
                classMetrics.numMethods = node.getMethods().length;

                // CBO - Coupling Between Objects (classes referenciadas)
                Set<String> coupledClasses = new HashSet<>();
                node.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(SimpleType simpleType) {
                        coupledClasses.add(simpleType.getName().getFullyQualifiedName());
                        return super.visit(simpleType);
                    }
                });
                classMetrics.cbo = coupledClasses.size();

                // DIT - Depth of Inheritance Tree
                classMetrics.dit = calculateDIT(node);

                // NOC - Number of Children (não podemos calcular sem todo o projeto)
                classMetrics.noc = 0; // Deixar como 0 por enquanto

                results.add(classMetrics);

                // Analisar métodos da classe
                for (MethodDeclaration method : node.getMethods()) {
                    MetricResult methodMetrics = analyzeMethod(method, cu, currentPackage, node.getName().getIdentifier());
                    results.add(methodMetrics);
                }

                return true;
            }
        });

        return results;
    }

    private MetricResult analyzeMethod(MethodDeclaration method, CompilationUnit cu, String packageName, String className) {
        MetricResult metrics = new MetricResult();
        metrics.type = ArtifactType.METHOD;
        metrics.packageName = packageName;
        metrics.className = className;
        metrics.methodName = method.getName().getIdentifier();

        // LOC do método
        int startLine = cu.getLineNumber(method.getStartPosition());
        int endLine = cu.getLineNumber(method.getStartPosition() + method.getLength());
        metrics.loc = endLine - startLine + 1;

        // Complexidade Ciclomática (McCabe)
        metrics.cyclomaticComplexity = calculateCyclomaticComplexity(method);

        return metrics;
    }

    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        final int[] complexity = {1}; // Começa com 1

        method.accept(new ASTVisitor() {
            @Override
            public boolean visit(IfStatement node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(ForStatement node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(EnhancedForStatement node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(WhileStatement node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(DoStatement node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(SwitchCase node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(CatchClause node) {
                complexity[0]++;
                return super.visit(node);
            }

            @Override
            public boolean visit(ConditionalExpression node) {
                complexity[0]++;
                return super.visit(node);
            }
        });

        return complexity[0];
    }

    private int calculateDIT(TypeDeclaration node) {
        int depth = 0;
        Type superclass = node.getSuperclassType();

        while (superclass != null) {
            depth++;
            // Não conseguimos navegar para a superclasse sem resolver bindings
            // Por enquanto, retornar 1 se tem superclasse, 0 se não tem
            break;
        }

        return depth;
    }
}