from jinja2 import Environment, FileSystemLoader
import os
from distutils.dir_util import copy_tree
from distutils.file_util import copy_file
from dsl.applang_exceptions import GeneratorException
from dsl.consts import PATH_APPCOMPAT_V7, PATH_GOOGLE_PLAY
from dsl.utils import generate_from_template, generate_once_from_template, \
    generate_overwrite_backup_from_template, \
    camel_to_under, print_log, query_yes_no, \
    XML_GEN_COMMENT, JAVA_GEN_COMMENT, XML_GEN_ONCE_COMMENT, JAVA_GEN_ONCE_COMMENT
from dsl.textx.semantics import TYPE_TEXT_IN_DB, TYPE_INT_IN_DB, TYPE_REAL_IN_DB


def generate_android(model, output_folder="../gen/", overwrite_manifest=True, eclipse_gen=True):
    """
    Generate the android application with the applang parser that contains the model.
    :param parser: The parser which was used to parse the applang language
    :param debug(bool): Debug mode
    :param output_folder(str): Folder to output the generated app
    :return:
    """
    print
    print_log("~~~~Generating the ANDROID application~~~~")
    print
    config = model.config
    android_specs = config.android_specs
    print_log("App name: {}".format(config.app_name))
    print_log("Namespace: {}".format(config.namespace))
    print_log("Full Qname: {}".format(config.qname))
    print_log("Min version: {}".format(android_specs.min_version))
    print_log("Target version: {}".format(android_specs.target_version))
    if android_specs.sdk_path is None:
        print_log("WARNING: The Android SDK Path is not set! "
                  "This will cause problems if the libraries haven't been copied yet!")
    if android_specs.sdk_path is not None and not os.path.exists(android_specs.sdk_path):
        raise GeneratorException("The SDK in the specified path '{}' does not exist!"
                                .format(android_specs.sdk_path))

    #Checking if the output folder exists. If it doesn't, creates it.
    android_gen_folder = output_folder + "/android/"
    if not os.path.exists(android_gen_folder):
        print_log("Folder for storing generated android applications does not exist. It will be created: {}"
                  .format(android_gen_folder))
        os.makedirs(android_gen_folder)
    output_app_folder = android_gen_folder + config.namespace.replace('.', '/') + "/" + config.app_name + "/"
    if not os.path.exists(output_app_folder):
        print_log("Folder for storing the generated android application does not exist. It will be created: {}"
                  .format(android_gen_folder))
        os.makedirs(output_app_folder)

    print
    #Checking if the V7 appcompat library exists.
    #If it doesn't, copies it from the Android SDK.
    if os.path.exists(android_gen_folder + "appcompat/"):
        print_log("Appcompat library exists. No need to copy it from the android SDK.")
    else:
        print_log("Appcompat does not exist in the generated folder. Copying in from the android SDK.")
        copy_tree(src=android_specs.sdk_path + PATH_APPCOMPAT_V7, dst=android_gen_folder + "appcompat/")

    #Checking if the Google Play Services library (needed for Google maps) exists.
    #If it doesn't, copies it from the Android SDK.
    # if os.path.exists(android_gen_folder + "google-play-services_lib/"):
    #     print_log("Google Play Services library exists. No need to copy it from the android SDK.")
    # else:
    #     print_log("Google Play Services library does not exist in the generated folder. "
    #               "Copying in from the android SDK.")
    #     copy_tree(src=android_specs.sdk_path + PATH_GOOGLE_PLAY,
    #               dst=android_gen_folder + "google-play-services_lib/")


    #Framework copying
    print
    print_log("Copying the app framework to the output folder: {}".format(output_app_folder))
    copy_tree(src="../../frameworks/android", dst=android_gen_folder)
    print_log("Finished copying the app framework.")
    print

    #File generation
    environment = Environment(loader=FileSystemLoader('../templates/android'))
    environment.filters['cameltounder'] = camel_to_under
    environment.trim_blocks = True
    environment.lstrip_blocks = True
    environment.globals['xml_gen_comment'] = XML_GEN_COMMENT
    environment.globals['java_gen_comment'] = JAVA_GEN_COMMENT
    environment.globals['xml_gen_once_comment'] = XML_GEN_ONCE_COMMENT
    environment.globals['java_gen_once_comment'] = JAVA_GEN_ONCE_COMMENT

    if eclipse_gen or \
        query_yes_no("Do you want to generate "
                     "(and possibly overwrite) the eclipse files (.project and .classpath)?"):
        print_log("Generating the eclipse files")
        copy_file(src="../../frameworks/eclipse_files/.classpath", dst=output_app_folder)
        generate_from_template(environment, "dot.project.tmpl", output_app_folder, ".project",
                               config=config)
        print_log("Finished generating the eclipse files")

    # AndroidManifest.xml generation
    generate_overwrite_backup_from_template(environment, "AndroidManifest.tmpl", output_app_folder,
                                            "AndroidManifest.xml",
                                            overwrite_manifest, config=config, android_specs=android_specs,
                                            entities=model.entities,
                                            has_image=[attr for entity in model.entities for attr in entity.attributes
                                                       if attr.primitive_type and attr.primitive_type == 'image'])
    # project.properties generation
    generate_from_template(environment, "project.properties.tmpl", output_app_folder, "project.properties",
                           android_specs=android_specs, namespace_dot_count=config.namespace.count('.'))
    # res/values folder creation
    output_gen_values_folder = output_app_folder + "res/values/"
    if not os.path.exists(output_gen_values_folder):
        os.makedirs(output_gen_values_folder)
    # libs folder creation
    output_gen_libs_folder = output_app_folder + "libs/"
    if not os.path.exists(output_gen_libs_folder):
        os.makedirs(output_gen_libs_folder)
    # assets folder creation
    output_gen_assets_folder = output_app_folder + "assets/"
    if not os.path.exists(output_gen_assets_folder):
        os.makedirs(output_gen_assets_folder)

    # gen_string_entities.xml generation
    generate_from_template(environment, "gen_strings_entities.xml.tmpl", output_gen_values_folder,
                           "gen_strings_entities.xml", 
                           app_label=config.app_label, entities=model.entities)
    # gen_arrays.xml generation
    generate_from_template(environment, "gen_arrays.xml.tmpl", output_gen_values_folder,
                           "gen_arrays.xml", 
                           config=config, entities=model.entities)

    output_src_folder = output_app_folder + "src/" + config.qname.replace(".", "/") + "/"
    if not os.path.exists(output_src_folder):
        os.makedirs(output_src_folder)

    # src gen folder creation
    output_src_gen_folder = output_app_folder + "src-gen/" + config.qname.replace(".", "/") + "/"
    if not os.path.exists(output_src_gen_folder):
        os.makedirs(output_src_gen_folder)
    # databases folder creation
    output_src_gen_databases_folder = output_src_gen_folder + "databases" + "/"
    if not os.path.exists(output_src_gen_databases_folder):
        os.makedirs(output_src_gen_databases_folder)
    # content_providers folder creation
    output_src_gen_content_providers_folder = output_src_gen_folder + "content_providers" + "/"
    if not os.path.exists(output_src_gen_content_providers_folder):
        os.makedirs(output_src_gen_content_providers_folder)
    # adapters folder creation
    output_src_gen_adapters_folder = output_src_gen_folder + "adapters" + "/"
    if not os.path.exists(output_src_gen_adapters_folder):
        os.makedirs(output_src_gen_adapters_folder)
    # fragments folder creation
    output_src_gen_fragments_folder = output_src_gen_folder + "fragments" + "/"
    if not os.path.exists(output_src_gen_fragments_folder):
        os.makedirs(output_src_gen_fragments_folder)
    # res/layout folder creation
    output_gen_layout_folder = output_app_folder + "res/layout/"
    if not os.path.exists(output_gen_layout_folder):
        os.makedirs(output_gen_layout_folder)

    # DatabaseOpenHelper.java
    generate_from_template(environment, "DatabaseOpenHelper.java.tmpl", output_src_gen_databases_folder,
                           "DatabaseOpenHelper.java", 
                           config=config, entities=model.entities)
    for entity in model.entities:
        # Used for import statements in the code.
        ref_entity_names = {attr.reference_type.name for attr in entity.attributes if attr.reference_type}
        # User for view_from_container
        view_container_attrs = [(entity_container, attr) for entity_container in model.entities for attr in entity_container.attributes
            if attr.reference_type and attr.reference_type.name == entity.name and attr.view_from_container]
        view_container_ref_entity_names = {x[0].name for x in view_container_attrs}
        # {{Entity}}Table.java
        generate_from_template(environment, "EntityTable.java.tmpl", output_src_gen_databases_folder,
                               "{}Table.java".format(entity.name),
                               config=config, entity=entity, TYPE_TEXT_IN_DB=TYPE_TEXT_IN_DB,
                               TYPE_INT_IN_DB=TYPE_INT_IN_DB, TYPE_REAL_IN_DB=TYPE_REAL_IN_DB)
        #{{Entity}}ContentProvider.java
        generate_from_template(environment, "EntityContentProvider.java.tmpl", output_src_gen_content_providers_folder,
                               "{}ContentProvider.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names)
        #{{Entity}}ListFragment.java
        generate_from_template(environment, "EntityListFragment.java.tmpl", output_src_gen_fragments_folder,
                               "{}ListFragment.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names)
        #{{Entity}}DetailsFragment.java
        generate_from_template(environment, "EntityDetailsFragment.java.tmpl", output_src_gen_fragments_folder,
                               "{}DetailsFragment.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names,
                               view_container_attrs=view_container_attrs,
                               view_container_ref_entity_names=view_container_ref_entity_names)
        #{{Entity}}Adapters.java
        generate_from_template(environment, "EntityAdapter.java.tmpl", output_src_gen_adapters_folder,
                               "{}Adapter.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names)
        #{{Entity}}NewFragment.java
        generate_from_template(environment, "EntityNewFragment.java.tmpl", output_src_gen_fragments_folder,
                               "{}NewFragment.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names)
        #{{Entity}}EditFragment.java
        generate_from_template(environment, "EntityEditFragment.java.tmpl", output_src_gen_fragments_folder,
                               "{}EditFragment.java".format(entity.name),
                               config=config, entity=entity, ref_entity_names=ref_entity_names)
        generate_from_template(environment, "gen_list_item_entity.xml.tmpl", output_gen_layout_folder,
                               "gen_list_item_{}.xml".format(camel_to_under(entity.name)),
                               entity=entity)
        generate_from_template(environment, "gen_fragment_entity_details.xml.tmpl", output_gen_layout_folder,
                               "gen_fragment_{}_details.xml".format(camel_to_under(entity.name)),
                               entity=entity, view_container_attrs=view_container_attrs)
        generate_from_template(environment, "gen_fragment_entity_new.xml.tmpl", output_gen_layout_folder,
                               "gen_fragment_{}_new.xml".format(camel_to_under(entity.name)),
                               entity=entity)
        generate_from_template(environment, "gen_fragment_entity_edit.xml.tmpl", output_gen_layout_folder,
                               "gen_fragment_{}_edit.xml".format(camel_to_under(entity.name)),
                               entity=entity)

    # Generating impl classes ONCE
    output_src_impl_databases_folder = output_src_folder + "impl/databases" + "/"
    if not os.path.exists(output_src_impl_databases_folder):
        os.makedirs(output_src_impl_databases_folder)
    output_src_impl_fragments_folder = output_src_folder + "impl/fragments" + "/"
    if not os.path.exists(output_src_impl_fragments_folder):
        os.makedirs(output_src_impl_fragments_folder)
    # DatabaseOpenHelperImpl.java generate
    generate_once_from_template(environment, "DatabaseOpenHelperImpl.java.tmpl", output_src_impl_databases_folder,
                                "DatabaseOpenHelperImpl.java",
                                config=config)
    for entity in model.entities:
        generate_once_from_template(environment, "EntityListFragmentImpl.java.tmpl", output_src_impl_fragments_folder,
                                    "{}ListFragmentImpl.java".format(entity.name),
                                    config=config, entity=entity)
        generate_once_from_template(environment, "EntityNewFragmentImpl.java.tmpl", output_src_impl_fragments_folder,
                                    "{}NewFragmentImpl.java".format(entity.name),
                                    config=config, entity=entity)
        generate_once_from_template(environment, "EntityEditFragmentImpl.java.tmpl", output_src_impl_fragments_folder,
                                    "{}EditFragmentImpl.java".format(entity.name),
                                    config=config, entity=entity)
        generate_once_from_template(environment, "EntityDetailsFragmentImpl.java.tmpl", output_src_impl_fragments_folder,
                                    "{}DetailsFragmentImpl.java".format(entity.name),
                                    config=config, entity=entity)

    print
    print_log("Finished generating the ANDROID application")
    print
