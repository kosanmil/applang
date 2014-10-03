from jinja2 import Environment, FileSystemLoader
import os
from distutils.dir_util import copy_tree
from distutils.file_util import copy_file
from dsl.applang_exceptions import GeneratorException
from dsl.consts import PATH_APPCOMPAT_V7, PATH_GOOGLE_PLAY
from dsl.utils import generate_from_template, camel_to_under


def generate_android(model, debug=False, output_folder="../gen/", overwrite_all=True, eclipse_gen=True):
    """
    Generate the android application with the applang parser that contains the model.
    :param parser: The parser which was used to parse the applang language
    :param debug(bool): Debug mode
    :param output_folder(str): Folder to output the generated app
    :return:
    """
    print
    print("~~~~Generating the ANDROID application~~~~")
    print
    config = model.config
    android_specs = config.android_specs
    print("App name: {}".format(config.app_name))
    print("Namespace: {}".format(config.namespace))
    print("Full Qname: {}".format(config.qname))
    print("Min version: {}".format(android_specs.min_version))
    print("Target version: {}".format(android_specs.target_version))
    if android_specs.sdk_path is None:
        print("WARNING: The Android SDK Path is not set! "
              "This will cause problems if the libraries haven't been copied yet!")
    if android_specs.sdk_path is not None and not os.path.exists(android_specs.sdk_path):
        raise GeneratorException("The SDK in the specified path '{}' does not exist!"
                                .format(android_specs.sdk_path))

    #Checking if the output folder exists. If it doesn't, creates it.
    android_gen_folder = output_folder + "/android/"
    if not os.path.exists(android_gen_folder):
        print("Folder for storing generated android applications does not exist. It will be created: {}"
              .format(android_gen_folder))
        os.makedirs(android_gen_folder)
    output_folder = android_gen_folder + config.qname + "/"
    if not os.path.exists(output_folder):
        print("Folder for storing the generated android application does not exist. It will be created: {}"
              .format(android_gen_folder))
        os.makedirs(output_folder)

    print
    #Checking if the V7 appcompat library exists.
    #If it doesn't, copies it from the Android SDK.
    if os.path.exists(android_gen_folder + "appcompat/"):
        print("Appcompat library exists. No need to copy it from the android SDK.")
    else:
        print("Appcompat does not exist in the generated folder. Copying in from the android SDK.")
        copy_tree(src=android_specs.sdk_path + PATH_APPCOMPAT_V7, dst=android_gen_folder + "appcompat/")

    #Checking if the Google Play Services library (needed for Google maps) exists.
    #If it doesn't, copies it from the Android SDK.
    if os.path.exists(android_gen_folder + "google-play-services_lib/"):
        print("Google Play Services library exists. No need to copy it from the android SDK.")
    else:
        print("Google Play Services library does not exist in the generated folder. "
              "Copying in from the android SDK.")
        copy_tree(src=android_specs.sdk_path + PATH_GOOGLE_PLAY,
                  dst=android_gen_folder + "google-play-services_lib/")


    #Framework copying
    print
    print("Copying the app framework to the output folder: {}".format(output_folder))
    copy_tree(src="../../frameworks/android", dst=output_folder)
    print("Finished copying the app framework.")
    print

    #File generation
    environment = Environment(loader=FileSystemLoader('../templates/android'))
    environment.filters['cameltounder'] = camel_to_under
    environment.trim_blocks = True
    environment.lstrip_blocks = True

    #ToDo create query_yes_no for Eclipse
    if eclipse_gen:
        print("Generating the eclipse files")
        copy_file(src="../../frameworks/eclipse_files/.classpath", dst=output_folder)
        generate_from_template(environment, "dot.project.tmpl", output_folder, ".project", overwrite_all,
                               config=config)
        print("Finished generating the eclipse files")

    # AndroidManifest.xml generation
    generate_from_template(environment, "AndroidManifest.tmpl", output_folder, "AndroidManifest.xml", overwrite_all,
                           config=config, android_specs=android_specs)
    # project.properties generation
    generate_from_template(environment, "project.properties.tmpl", output_folder, "project.properties", overwrite_all,
                           config=config, android_specs=android_specs)
    # gen_string_entities.xml generation
    generate_from_template(environment, "gen_strings_entities.xml.tmpl", output_folder + "res/values/", "gen_strings_entities.xml", overwrite_all,
                           app_label=config.app_label, entities=model.entities)
    # string.xml generation
    # generate_from_template(environment, "strings_res.tmpl", output_folder + "res/values/", "strings.xml", overwrite_all,
    #                        config=config, android_specs=android_specs)

    output_src_folder = output_folder + "src/" + config.qname.replace(".", "/") + "/"
    if not os.path.exists(output_src_folder):
        os.makedirs(output_src_folder)
    generate_from_template(environment, "MainActivity.java.tmpl", output_src_folder, "MainActivity.java", overwrite_all,
                           config=config)
    print
    print("Finished generating the ANDROID application")
    print
