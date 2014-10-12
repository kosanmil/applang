import sys
import os
import re
import codecs
from time import strftime
from shutil import copyfile


#Template filters
first_cap_re = re.compile('(.)([A-Z][a-z]+)')
all_cap_re = re.compile('([a-z0-9])([A-Z])')


def camel_to_under(name):
    """
    Converts CamelCase to underscore with lowercase. Example: CamelCase -> camel_case
    """
    s1 = first_cap_re.sub(r'\1_\2', name)
    return all_cap_re.sub(r'\1_\2', s1).lower()

#Template filters end


def generate_from_template(template_env, template_name,
                           output_folder, output_file_name, **kwargs):
    """
    Generates the file according to the Jinja2 template.
    :param template_env: Enviroment used to find the Jinja2 template
    :param template_name: Name of the template file used.
    :param output_folder(str): Path of the folder that contains the file to be outputed
    :param output_file_name(str): Name of the file in the output_folder that will be generated.
    :param overwrite(bool): If True, overwrites the file.
                            If False, user will be asked if the file should be overwritten
    :param kwargs:
    :return:
    """
    print
    output_file_path = output_folder + output_file_name
    print_log("Generating {}".format(output_file_name))
    _generate_file(template_env, template_name, output_file_path, **kwargs)
    print_log("Finished generating {}".format(output_file_name))


def generate_once_from_template(template_env, template_name,
                           output_folder, output_file_name, **kwargs):
    """
    Generates the file according to the Jinja2 template if the file does not exist. If it exists, does nothing.
    :param template_env: Enviroment used to find the Jinja2 template
    :param template_name: Name of the template file used.
    :param output_folder(str): Path of the folder that contains the file to be outputed
    :param output_file_name(str): Name of the file in the output_folder that will be generated.
    :param overwrite(bool): If True, overwrites the file.
                            If False, user will be asked if the file should be overwritten
    :param kwargs:
    :return:
    """
    print
    output_file_path = output_folder + output_file_name
    if os.path.exists(output_file_path):
        print_log("File {} already exists. Will not generate it.".format(output_file_name))
        return
    print_log("Generating {} ONCE.".format(output_file_name))
    _generate_file(template_env, template_name, output_file_path, **kwargs)
    print_log("Finished generating {} ONCE.".format(output_file_name))


def generate_overwrite_backup_from_template(template_env, template_name,
                           output_folder, output_file_name, overwrite=True,
                           **kwargs):
    """
    Generates the file according to the Jinja2 template if the file does not exist. If it exists, does nothing.
    :param template_env: Enviroment used to find the Jinja2 template
    :param template_name: Name of the template file used.
    :param output_folder(str): Path of the folder that contains the file to be outputed
    :param output_file_name(str): Name of the file in the output_folder that will be generated.
    :param overwrite(bool): If True, overwrites the file and creates a backup.
                            If False, user will be asked if the file should be overwritten
    :param kwargs:
    :return:
    """
    print
    output_file_path = output_folder + output_file_name
    if os.path.exists(output_file_path):
        if overwrite or query_yes_no("Do you want to override the file '{}' It will create a backup".
                format(output_file_name)):
            print_log("Creating backup of file {}.".format(output_file_name))
            copyfile(output_file_path, output_file_path + ".bak")
            print_log("Finished creating backup of file {}.".format(output_file_name))
        else:
            return
    print_log("Generating {}.".format(output_file_name))
    _generate_file(template_env, template_name, output_file_path, **kwargs)
    print_log("Finished generating {}.".format(output_file_name))


def _generate_file(template_env, template_name,
                           output_file_path, **kwargs):
    file = codecs.open(output_file_path, "w+", encoding="utf-8")
    file.write(template_env.get_template(template_name)
                       .render(kwargs))
    file.close()


def list_platforms(platforms):
    retList = []
    if platforms.android:
        retList.append('android')
    if platforms.ios:
        retList.append('ios')
    if platforms.windows_phone:
        retList.append('windows phone')
    return retList


def query_yes_no(question, default="yes"):
    """Ask a yes/no question via raw_input() and return their answer.

    "question" is a string that is presented to the user.
    "default" is the presumed answer if the user just hits <Enter>.
        It must be "yes" (the default), "no" or None (meaning
        an answer is required of the user).

    The "answer" return value is one of "yes" or "no".
    """
    valid = {"yes": True, "y": True, "ye": True,
             "no": False, "n": False}
    if default is None:
        prompt = " [y/n] "
    elif default == "yes":
        prompt = " [Y/n] "
    elif default == "no":
        prompt = " [y/N] "
    else:
        raise ValueError("invalid default answer: '%s'" % default)

    while True:
        sys.stdout.write(question + prompt)
        choice = raw_input().lower()
        if default is not None and choice == '':
            return valid[default]
        elif choice in valid:
            return valid[choice]
        else:
            sys.stdout.write("Please respond with 'yes' or 'no' "
                             "(or 'y' or 'n').\n")


def print_log(text):
    print("[{}] {}".format(strftime("%H:%M:%S"), text))


XML_GEN_COMMENT = """<!--
   This file has been generated using the AppLang mobile application language.
   DO NOT MODIFY IT! Any changes will be overridden!
-->"""

JAVA_GEN_COMMENT = """/*
   This file has been generated using the AppLang mobile application language.
   DO NOT MODIFY IT! Any changes will be overridden!
*/"""

JAVA_GEN_ONCE_COMMENT = """/*
   This file has been generated ONCE using the AppLang mobile application language.
   Changes will not be overriden, so the file can be modified.
*/"""

XML_GEN_ONCE_COMMENT = """<!--
   This file has been generated ONCE using the AppLang mobile application language.
   Changes will not be overriden, so the file can be modified.
-->"""