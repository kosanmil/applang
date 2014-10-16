from dsl.applang_exceptions import UnimplementedPlatformException


def gen_ios(model, output_folder, overwrite_all = True):
    """
    Generate the ios application with the applang parser that contains the model.
    :param parser: The parser which was used to parse the applang language
    :param debug: Debug mode
    :param output_folder: Folder to output the generated app
    :return:
    """
    raise UnimplementedPlatformException("iOS generation not yet implemented")
