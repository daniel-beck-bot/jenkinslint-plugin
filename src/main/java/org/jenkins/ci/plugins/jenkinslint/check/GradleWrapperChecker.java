package org.jenkins.ci.plugins.jenkinslint.check;

import hudson.matrix.MatrixProject;
import hudson.model.Item;
import hudson.model.Project;
import hudson.tasks.Builder;
import org.jenkins.ci.plugins.jenkinslint.model.AbstractCheck;

import java.util.List;
import java.util.logging.Level;

/**
 * @author Victor Martinez
 */
public class GradleWrapperChecker extends AbstractCheck {

    public GradleWrapperChecker() {
        super();
        this.setDescription("By distributing the wrapper with your project, anyone can work with it without needing to " +
                            "install Gradle beforehand.<br/> Even better, users of the build are guaranteed to use the " +
                            "version of Gradle that the build was designed to work with.<br/>" +
                            "<a href=https://docs.gradle.org/current/userguide/gradle_wrapper.html>The Gradle Wrapper</a>");
        this.setSeverity("Medium");
    }

    public boolean executeCheck(Item item) {
        boolean found = false;
        //if (Jenkins.getInstance().pluginManager.getPlugin("gradle") != null) {
        if (item.getClass().getName().endsWith("hudson.maven.MavenModuleSet")) {
            found = false;
        } else {
            if (item instanceof Project) {
                found = isGradlew(((Project) item).getBuilders());
            }
            if (item instanceof MatrixProject) {
                found = isGradlew(((MatrixProject) item).getBuilders());
            }
        }
        //}
        return found;
    }

    private boolean isGradlew (List<Builder> builders) {
        boolean status = false;
        if (builders != null && builders.size() > 0 ) {
            for (Builder builder : builders) {
                if (builder.getClass().getName().endsWith("Gradle")) {
                    try {
                        Object isUseWrapper = builder.getClass().getMethod("isUseWrapper", null).invoke(builder);
                        if (isUseWrapper instanceof Boolean) {
                            status = ! ((Boolean) isUseWrapper).booleanValue();
                            LOG.log(Level.FINE, "isGradlew " + !status);
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Exception " + e.getMessage(), e.getCause());
                        status = false;
                    }
                }
            }
        }
        return status;
    }
}
