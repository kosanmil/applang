from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from textx.exceptions import TextXSemanticError
from dsl.consts import DEF_ANDROID_TARGET_VER, DEF_ANDROID_MIN_VER
from dsl.utils import camel_to_under

TYPE_TEXT_IN_DB = ['string', 'numeric_string', 'textarea_string', 'image', 'telephone_type', 'address_type']
TYPE_INT_IN_DB = ['int', 'bool', 'date']
TYPE_REAL_IN_DB = ['float']


def get_semantic_model_from_file(applang_file, metamodel_file, export_to_dot=False):
    metamodel = metamodel_from_file(metamodel_file)
    if export_to_dot:
        print ('Exporting metamodel to dot')
        metamodel_export(metamodel, 'applang_meta.dot')
        print ('Exporting metamodel to dot completed')
    model = metamodel.model_from_file(applang_file)

    print ('Checking model semantics')
    model = check_semantics(model)
    print ('Checking model semantics completed')

    if export_to_dot:
        print ('Exporting model to dot')
        model_export(model, 'applang_model.dot')
        print ('Exporting model to dot completed')
    return model


def check_semantics(model):
    # Check config model
    if not model.config:
        raise TextXSemanticError('Config is required')

    # If app label does not exist, put app name instead.
    if not model.config.app_label:
        model.config.app_label = model.config.app_name
    model.config.qname = model.config.namespace + '.' + camel_to_under(model.config.app_name)
    if model.config.platforms.android and not model.config.android_specs:
        raise TextXSemanticError('Android specs are required if the android platform is specified')
    android_specs = model.config.android_specs
    if not android_specs:
        android_specs.min_version = DEF_ANDROID_MIN_VER
        android_specs.target_version = DEF_ANDROID_TARGET_VER
        model.config.android_specs = android_specs
    else:
        if not android_specs.min_version:
            android_specs.min_version = DEF_ANDROID_MIN_VER
        if not android_specs.target_version:
            android_specs.target_version = DEF_ANDROID_TARGET_VER
        if android_specs.min_version > android_specs.target_version:
            raise TextXSemanticError('In Android Specs: Minimum version cannot be greater than the target version')
    print("{} {} {}".format(android_specs.sdk_path, android_specs.min_version, android_specs.target_version))
    print(model.config.qname)

    if not model.entities:
        raise TextXSemanticError('At least one entity is required')
    #Checking duplicate entity names
    entity_names = [x.name for x in model.entities]
    if len(entity_names) != len(set(entity_names)):
        raise TextXSemanticError('Duplicate entity names detected!')
    for entity in model.entities:
        # If entity label does not exist, put entity name instead.
        if not entity.label:
            entity.label = entity.name

        attribute_names = [x.name for x in entity.attributes]
        if len(attribute_names) != len(set(attribute_names)):
            raise TextXSemanticError('Duplicate attribute names in entity "{}" detected!'.format(entity.name))

        for attribute in entity.attributes:
            # If attribute label does not exist, put attribute name instead.
            if not attribute.label:
                attribute.label = attribute.name.title()
            if attribute.view_from_container and not attribute.reference_type:
                raise TextXSemanticError("Cannot have 'viewFromContainer' on an non-reference type, in attribute '{}', entity '{}'".format(attribute.name, entity.name))

    return model


if __name__ == "__main__":
    get_semantic_model_from_file("example_textx.alang", 'applang.tx', export_to_dot=True)
