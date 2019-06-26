package se.omegapoint.hackathon.jcalassert.processor;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({
        "se.omegapoint.hackathon.jcalassert.processor.Assert",
        "se.omegapoint.hackathon.jcalassert.processor.Assert.List"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AssertProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<Element> annotatedElements = new HashSet<>();
        annotatedElements.addAll(roundEnv.getElementsAnnotatedWith(Assert.class));
        annotatedElements.addAll(roundEnv.getElementsAnnotatedWith(Assert.List.class));

        ElementFilter.methodsIn(annotatedElements).forEach(this::process);

        return false;
    }

    private void process(ExecutableElement methodElement) {
        Assert[] asserts = methodElement.getAnnotationsByType(Assert.class);
        String methodName = methodElement.getSimpleName().toString();

        TypeElement classElement = (TypeElement) methodElement.getEnclosingElement();
        String packageName = packageNameOf(classElement);
        String className = classElement.getSimpleName().toString();
        String testClassName = className + capitalize(methodName) + "Test";

        try (PrintWriter out = new PrintWriter(processingEnv.getFiler()
                .createSourceFile(packageName + "." + testClassName).openWriter())) {

            out.println("package " + packageName + ";");
            out.println("import org.junit.Test;");
            out.println("import static org.junit.Assert.*;");
            out.println("public class " + testClassName + " {");

            for (Assert a : asserts) {
                boolean value = a.that();
                boolean expected = a.returns();

                out.println("  @Test");
                out.println("  public void " + methodName + capitalize(value) + capitalize(expected) + "() {");
                out.println("    " + className + " o = new " + className + "();");
                out.println("    assertEquals(" + expected + ", o." + methodName + "(" + value + "));");
                out.println("  }");
            }

            out.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String packageNameOf(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
    }

    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static String capitalize(Object value) {
        return capitalize(String.valueOf(value));
    }
}
