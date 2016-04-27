package com.junliqin.toolbox

import org.gradle.api.Plugin
import org.gradle.api.Project

public class FirmUploadPlugin implements Plugin<Project>
{

    @Override
    void apply(Project project) {
        project.extensions.create("fir", FirExtension)

        project.android.applicationVariants.all { var ->

            println("prepare input on plugin ")
            def File input = var.outputs[0].outputFile
            def uploadTask = project.task("firmUpload${var.name}",
                    type:FirTask) {

                inputFile = var.outputs[0].outputFile
                variant = var

                dependsOn var.assemble

            }

            uploadTask.dependsOn var.assemble

            uploadTask.group = 'fir'
            uploadTask.description = "Upload build variant apk to fir.im"
        }

    }
}