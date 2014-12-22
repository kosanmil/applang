#######################################################################
# Name: generator.py
# Purpose: Textual DSL for generating mobile applications
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from datetime import datetime
import os.path

from applang.exceptions import UnimplementedPlatformException
from applang.generator_android import gen_android
from applang.generator_ios import gen_ios
from applang.generator_wp import gen_windows_phone
from applang.semantics import get_semantic_model_from_file
from applang.utils import print_log, list_platforms


package_directory = os.path.dirname(os.path.abspath(__file__))


def gen_apps_from_textx_file(file_path, output_folder,
                             overwrite_manifest=True, eclipse_gen=True, export_to_dot=True):
    start_time = datetime.now()
    if not os.path.isfile(file_path):
        raise IOError('TextX file does not exist: {}'.format(os.path.abspath(file_path)))
    print_log("TextX file path: {}".format(os.path.abspath(file_path)))
    metamodel_file_path = os.path.join(package_directory, "applang.tx")
    print_log("Generating apps from textX file")
    model = get_semantic_model_from_file(file_path, metamodel_file_path, export_to_dot=export_to_dot)
    platforms = model.config.platforms
    print_log("Platforms specified: {}".format(list_platforms(platforms)))
    if platforms.android:
        try:
            gen_android(model, output_folder, overwrite_manifest, eclipse_gen)
        except UnimplementedPlatformException:
            print_log("WARNING: Android generation is not implemented!")
    if platforms.ios:
        try:
            gen_ios(model, output_folder, overwrite_manifest)
        except UnimplementedPlatformException:
            print_log("WARNING: iOS generation is not implemented!")
    if platforms.windows_phone:
        try:
            gen_windows_phone(model, output_folder, overwrite_manifest)
        except UnimplementedPlatformException:
            print_log("WARNING: Windows Phone generation is not implemented!")
    end_time = datetime.now()
    time_diff = end_time - start_time
    print_log("Finished generating the applications for the following platforms: {}.".format(list_platforms(platforms)))
    if time_diff.seconds == 1:
        print_log("Time needed: {} second.".format(time_diff.seconds))
    else:
        print_log("Time needed: {} seconds.".format(time_diff.seconds))

if __name__ == "__main__":
    gen_apps_from_textx_file("example_textx.alang", os.path.join(os.path.pardir, 'gen/novipath'),
                                  overwrite_manifest=True, eclipse_gen=True)