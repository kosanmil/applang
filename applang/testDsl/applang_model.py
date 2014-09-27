#######################################################################
# Name: applang_model.py
# Purpose: Model for the applang DSL
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from pprint import pprint


class Entity:

    def __init__(self, name, attributes, label=None):
        self.name = name
        self.attributes = attributes
        self.label = label

    def __repr__(self):
        return str(self.__dict__)


class Attribute:

    def __init__(self, name, type, descriptions=None, many=False):
        self.name = name
        self.type = type
        self.descriptions = descriptions
        self.many = many

    def __repr__(self):
        return str(self.__dict__)