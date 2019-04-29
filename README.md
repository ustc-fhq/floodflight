This is the POF protocol library in ONOS.

- branch floodlight-pof

  To support ovs-pof, the original floodlightpof stack will be modified. See more instructions shown in [ONOS buck build](https://docs.google.com/document/d/1hAqBDFry2f4w9lMCAY_ieO04nWLR4gHV0uJrgV-8ovE/edit#heading=h.69a07zqxoy0r).
  
  Sometimes you want to test a pre-built jar that has not been published to maven repo. You can drop the jar anywhere in any subdirectory relative to the ```BUCK``` file. Then, use a prebuilt_jar rule to make it available in ```Buck```:
  ```
  prebuilt_jar (
    name = 'foo',
    binary_jar = 'lib/foo.jar',
    maven_coords = 'org.onosproject:foo:1.0.0-SNAPSHOT',
    visibility = [ 'PUBLIC' ],
  )
  ```
  
  You can add "foo" to the list of deps in your onos_jar / osgi_jar... rule.
