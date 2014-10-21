from setuptools import setup, find_packages

setup(name='Applang',
      version='0.1',
      description='DSL for generation of mobile applications',
      author='Milan Kosanovic',
      author_email='kosanmil@gmail.com',
      url='https://github.com/kosanmil/applang',
      packages=find_packages(exclude=["*.old_dsl", 'old_dsl','*.old_dsl.*']),
      include_package_data=True,
      install_requires=["textX"]
     )