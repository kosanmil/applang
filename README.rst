applang
=======

Applang (APPlication LANGuage) is a domain specific language (DSL) for specification and rapid development
of applications for mobile devices.

Currently it generates source code for Android applications, which are runnable upon generation. iOS and Windows Phone applications are planned for the future.

Installation
------------

Run ``setup.py install`` in the root folder of the repository using Python (tested with Python 2.7).
For example, the terminal command is

::

    python setup.py install

The installation will also installed the required package `textX`_.


Code generation
---------------

To generate the android source code of an application written in applang, use the ``gen_apps_from_textx_file`` function defined in the ``generator`` module. The function requires two parameters, the file that contains the application written in applang, and the output directory. 

The following example generates an application from the ``example_textx.alang`` file to the folder ``gen`` relative to the current folder.

::

    from generator import gen_apps_from_textx_file
    gen_apps_from_textx_file("example_textx.alang", "gen")


Grammar example
---------------

The following example shows the applang grammar written for an application that handles the following entities: 

Country, Town and Company. 

Countries can have multiple towns, Towns can have multiple companies, and Companies can have multiple subcompanies.

The grammar also contains the ``config``, which states information about the application, such as the name, namespace and platforms on which to generate.

::

  config {
      app_name = ApplangGenApp
      app_label = "Applang Generated App"
      namespace = applang
      db_version = 3
      start_screen = Country
      platforms = android, ios
      android_specs
      {
          sdk_path = "/home/kosan/software/android-sdk-linux"
      }
  }
  
  entity Country {
      operations(listall, create, edit, delete)
      attributes
      {
          code:
              string(required, unique, searchable, readonly)
  
          name:
              string(required, unique, searchable, toString)
  
          flag:
              image
      }
  }
  
  entity Town ("Town") {
      operations(edit, delete, listall, create)
      attributes
      {
          name:
              string(required, searchable, toString)
  
          postCode ("Post code"):
              numeric_string(required)
  
          country:
              Country(required, searchable, viewFromContainer)
  
          population:
              int
      }
      unique(name, country)
  }
  
  entity Company {
      operations(listall, create, edit, delete)
      attributes
      {
          name:
              string (required, searchable, toString ,readonly)
  
          vatNumber ("VAT number"):
              numeric_string (required, unique)
  
          domestic:
              bool (readonly, excludeFromList)
  
          town:
              Town (required, searchable, viewFromContainer)
  
          descr ("Description"):
              textarea_string (excludeFromList, readonly)
  
          parentCompany ("Parent company"):
              Company (viewFromContainer)
  
          telephone:
              telephone_type (excludeFromList)
  
          foundingDate("Founding date"):
              date (excludeFromList)
  
          address:
              address_type (excludeFromList)
      }


Generated application example
-----------------------------

The following images show screenshots from the application that was generated using the above grammar example.

Window that shows the list of countries (unaltered to the left, and altered using hand-written code to the right)

.. image:: https://raw.githubusercontent.com/kosanmil/applang/master/screenshots/country_list_altered.png

Window for listing and searching towns

.. image:: https://raw.githubusercontent.com/kosanmil/applang/master/screenshots/town_search.png

Windows for list of companies (left) and details of a specific company (right)

.. image:: https://raw.githubusercontent.com/kosanmil/applang/master/screenshots/company.png



.. _textX: https://github.com/igordejanovic/textX
