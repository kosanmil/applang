from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from textx.exceptions import TextXSemanticError
from dsl.consts import DEF_ANDROID_TARGET_VER, DEF_ANDROID_MIN_VER


def get_semantic_model_from_file(applang_file, metamodel_file, export_to_dot=False):
    metamodel = metamodel_from_file(metamodel_file)
    if export_to_dot:
        metamodel_export(metamodel, 'applang_meta.dot')
    model = metamodel.model_from_file(applang_file)
    model = check_semantics(model)
    if export_to_dot:
        model_export(model, 'applang_model.dot')
    return model


def check_semantics(model):
    # Check config model
    if not model.config:
        raise TextXSemanticError('Config is required')
    model.config.qname = model.config.namespace + '.' + model.config.app_name
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
        attribute_names = [x.name for x in entity.attributes]
        if len(attribute_names) != len(set(attribute_names)):
            raise TextXSemanticError('Duplicate attribute names in entity "{}" detected!'.format(entity.name))

    return model


if __name__ == "__main__":
    get_semantic_model_from_file("example_textx.alang", 'applang.tx', export_to_dot=True)
