#######################################################################
# Name: applang.py
# Purpose: Textual DSL for generating mobile applications
# Author: Milan Kosanovic <kosanmil AT gmail DOT com>
#
#######################################################################

from arpeggio import *
from arpeggio.export import PMDOTExporter, PTDOTExporter
from arpeggio import RegExMatch as _

def model():            return ZeroOrMore(entity), EOF
def entity():           return Kwd("entity"), FirstUpperID, Optional("(", STRING, ")"), attr_block
def attr_block():       return "{", ZeroOrMore(attribute), "}"
def attribute():        return Optional(MANY), ID, ":", attr_type, Optional(descr_block), ";"
def attr_type():        return ID


def descr_block():      return "(", descr, ZeroOrMore(",", descr), ")"
def descr():            return [required, unique, transient, toString, excludeFromList, label]
def required():         return Kwd("required")
def unique():           return Kwd("unique")
def transient():        return Kwd("transient")
def toString():         return Kwd("toString")
def excludeFromList():  return Kwd("excludeFromList")
def label():            return Kwd("label"), "=", STRING


def MANY():             return Kwd("many")


def ID():               return _(r'([a-z]|[A-Z]|_)([a-z]|[A-Z]|_|[0-9])*')
def FirstUpperID():     return _(r'([A-Z])([a-z]|[A-Z]|_|[0-9])*')
def STRING():           return _(r"""("(?:\\.|[^"\\])*")|('(?:\\.|[^'\\])*')""")


def comment():          return [_("//.*"), _("/\*.*\*/")]


# def number():     return _(r'\d*\.\d*|\d+')
# def factor():     return Optional(["+","-"]), [number,
#                           ("(", expression, ")")]
# def term():       return factor, ZeroOrMore(["*","/"], factor)
# def expression(): return term, ZeroOrMore(["+", "-"], term)
# def calc():       return OneOrMore(expression), EOF


# Semantic actions

class Entity(SemanticAction):
    """
    Semantic actions for entity. It checks if there are multiple entities with the same name, or
    if there are multiple attributes with the same name inside a single entity.
    """
    def first_pass(self, parser, node, children):
        #Check for duplicate entity names
        if not hasattr(parser, "entity_names"):
            parser.entity_names = []
        entity_name = children[0]
        if entity_name in parser.entity_names:
            raise SemanticError("Entity '{}' already exists!".format(entity_name))
        parser.entity_names.append(entity_name)

        #Check for duplicate attribute names in the same entity
        attribute_names = children[-1]
        if len(attribute_names) != len(set(attribute_names)):
            raise SemanticError("There are duplicate attribute names in the entity '{}'!".format(entity_name))


    def second_pass(self, parser, node):
        return


class AttributeBlock(SemanticAction):
    """
    Semantic actions for attribute. It returns all the attribute names of an entity on the first pass
    """
    def first_pass(self, parser, node, children):
        return children


class Attribute(SemanticAction):
    """
    Semantic actions for attribute. It returns the attribute name on the first pass
    """
    def first_pass(self, parser, node, children):
        if children[0] == MANY():
            return children[1]
        else:
            return children[0]


class DescrBlock(SemanticAction):
    """
    Semantic actions for description blocks. Returns the descriptions as a dictionary, or raises
    an semantic error if there are duplicate descriptions.
    """
    def first_pass(self, parser, node, children):
        retval = dict(children)
        if len(retval) != len(children):
            raise SemanticError("There are duplicate descriptions in the attribute!")
        return retval


class Required(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'required', True


class Unique(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'unique', True


class Transient(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'transient', True


class ToString(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'toString', True


class ExcludeFromList(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'excludeFromList', True


class Label(SemanticAction):

    def first_pass(self, parser, node, children):
        return 'label', children[0]


entity.sem = Entity()
attr_block.sem = AttributeBlock()
attribute.sem = Attribute()
descr_block.sem = DescrBlock()
required.sem = Required()
unique.sem = Unique()
transient.sem = Transient()
toString.sem = ToString()
excludeFromList.sem = ExcludeFromList()
label.sem = Label()


if __name__ == "__main__":

    input = """
    entity FirstEntity {
        //First entity
        name : String(required, unique, excludeFromList);
        age : Integer(label='Age of the entity');

    }

    entity SecondEntity ("Second Entity") {
        //Second entity
        many dfd : String;
        attr2 : Integer(toString, label="Attribute number 'two' .");

    }

    """

    pyParser = ParserPython(model, comment)

    PMDOTExporter().exportFile(pyParser.parser_model,
                               "applang_parse_tree_model.dot")

    parse_tree = pyParser.parse(input)

    # Then we export it to a dot file in order to visualise it.
    # This is also optional.
    PTDOTExporter().exportFile(parse_tree, "applang_parse_tree.dot")

    # getASG will start semantic analysis.
    # In this case semantic analysis will evaluate expression and
    # returned value will be the result of the input_expr expression.
    # print("{} = {}".format(input, parser.getASG()))
    pyParser.getASG()
