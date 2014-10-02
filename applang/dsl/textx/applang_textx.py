from textx.metamodel import metamodel_from_file
from textx.export import metamodel_export, model_export

# Create metamodel from textX description
applang_mm = metamodel_from_file('applang.tx')
# Export to dot
# Create png image with: dot -Tpng -O workflow_meta.dot
metamodel_export(applang_mm, 'applang_meta.dot')

# Load example model
example = applang_mm.model_from_file('example_textx.alang')
# Export to dot
# Create png image with: dot -Tpng -O example.dot
model_export(example, 'example.dot')

