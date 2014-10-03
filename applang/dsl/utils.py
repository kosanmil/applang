import sys
import os
import re


#Template filters
def camel_to_under(name):
    """
    Converts CamelCase to underscore with lowercase. Example: CamelCase -> camel_case
    """
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()
#Template filters end


def generate_from_template(template_env, template_name,
                           output_folder, output_file_name, overwrite=True,
                           **kwargs):
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
    if not overwrite and os.path.exists(output_file_path):
        if not query_yes_no("Do you want to overwrite {}?".format(output_file_name)):
            #Stopping the file generation
            return
    print("Generating {}".format(output_file_name))
    manifest_file = open(output_file_path, "w+")
    manifest_file.write(template_env.get_template(template_name)
                       .render(kwargs))
    manifest_file.close()
    print("Finished generating {}".format(output_file_name))


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
