from dsl.applang_exceptions import UnimplementedPlatformException


def generate_wp(parser, debug=False, output_folder="../gen/", overwrite_all = True):
    """
    Generate the windows phone application with the applang parser that contains the model.
    :param parser: The parser which was used to parse the applang language
    :param debug: Debug mode
    :param output_folder: Folder to output the generated app
    :return:
    """
    raise UnimplementedPlatformException("Windows phone generation not yet implemented")