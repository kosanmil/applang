from __builtin__ import hasattr

from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export
from textx.exceptions import TextXSemanticError

from applang.consts import DEF_ANDROID_TARGET_VER, DEF_ANDROID_MIN_VER
from applang.utils import camel_to_under, print_log


TYPE_TEXT_IN_DB = ['string', 'numeric_string', 'textarea_string', 'image', 'telephone_type', 'address_type']
TYPE_INT_IN_DB = ['int', 'bool', 'date']
TYPE_REAL_IN_DB = ['float']


def get_semantic_model_from_file(applang_file, metamodel_file, export_to_dot=False):
    metamodel = metamodel_from_file(metamodel_file)
    if export_to_dot:
        print_log('Exporting metamodel to dot')
        metamodel_export(metamodel, 'applang_metamodel.dot')
        print_log('Exporting metamodel to dot completed')
    model = metamodel.model_from_file(applang_file)

    print_log('Checking model semantics')
    model = check_semantics(model, metamodel)
    print_log('Checking model semantics completed')

    if export_to_dot:
        print_log('Exporting model to dot')
        model_export(model, 'applang_model.dot')
        print_log('Exporting model to dot completed')
    return model


def check_semantics(model, metamodel):
    parser = metamodel.parser
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
            raise TextXSemanticError('In Android Specs: Minimum version cannot be greater than the target version, {}'
                                     .format(print_position(parser, android_specs._position)))
    if model.config.start_screen:
        if not model.config.start_screen.operations.listall:
            raise TextXSemanticError("Start screen must be an entity that has the 'listall' operation, "
                                     "start screen entity = '{}', {}"
                                     .format(model.config.start_screen.name,
                                             print_position(parser, model.config._position)))

    if not model.entities:
        raise TextXSemanticError('At least one entity is required')
    if not [x for x in model.entities if x.operations.listall]:
        raise TextXSemanticError("At least one entity with the 'listAll' operation is required")
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
            raise TextXSemanticError('Duplicate attribute names in entity "{}" detected, {}'.format(entity.name,
                                             print_position(parser, entity._position)))

        for attr in entity.attributes:
            # If attribute label does not exist, put attribute name instead.
            if not attr.label:
                attr.label = attr.name.title()
            if attr.view_from_container and not attr.reference_type:
                raise TextXSemanticError(
                    "Cannot have 'viewFromContainer' on an non-reference type, in attribute '{}', entity '{}', {}"
                     .format(attr.name, entity.name, print_position(parser, attr._position)))
            #Setting toString of the entity
            if attr.to_string:
                if not attr.primitive_type or attr.primitive_type == 'image':
                    raise TextXSemanticError(
                        "toString attribute must be a non-image primitive type, in attribute '{}', entity '{}', {}"
                        .format(attr.name, entity.name, print_position(parser, attr._position)))
                if not hasattr(entity, 'to_string'):
                    entity.to_string = attr
                else:
                    raise TextXSemanticError(
                        "Entity cannot have more than one toString attribute, in attribute '{}'"
                        ", toString attribute '{}' entity '{}', {}"
                        .format(attr.name, entity.to_string.name, entity.name,
                                print_position(parser, attr._position)))
            if attr.searchable and attr.reference_type:
                if not hasattr(attr.reference_type, 'to_string'):
                    if len([ref_attr for ref_attr in attr.reference_type.attributes if ref_attr.to_string]) == 0:
                        raise TextXSemanticError(
                            "The entity that is reference by a searchable attribute must have a 'toString' attribute, "
                            "\nfrom searchable attribute '{}' in entity '{}', {}"
                            .format(attr.name, entity.name, print_position(parser, attr._position)))
        # Unique_set check
        for unique_set in entity.unique_sets:
            if len(unique_set.attributes) == 1:
                raise TextXSemanticError("Unique set must contain at least two attributes, in entity {}, {}"
                                         .format(entity.name, print_position(parser, unique_set._position)))
            for attr in unique_set.attributes:
                if attr.name not in [x.name for x in entity.attributes]:
                    raise TextXSemanticError("Unique set must only contain attributes from the parent entity {}. "
                                             "Attribute {} does not belong here, {}"
                                             .format(entity.name, attr.name,
                                                     print_position(parser, unique_set._position)))

    return model


def print_position(parser, position):
    return 'at {}'.format(parser.pos_to_linecol(position))


if __name__ == "__main__":
    get_semantic_model_from_file("example_textx.alang", 'applang.tx', export_to_dot=True)
