
Copyright (C) 2014 Google Inc.

# 'comments' backend server, configured to run on the App Engine Java VM Runtime.

To build, do the following:

1. Change the value of the `application` element in your `appengine-web.xml` to the app
id of an app that has been whitelisted for the VM Runtime.
2. Run `mvn package`
3. Run the `appcfg.sh` script from the VM Runtime SDK as follows:

        $ $SDK_DIR/bin/appcfg.sh -s preview.appengine.google.com update target/comments-1.0-SNAPSHOT

4. Visit `http://your-app-id.appspot.com/`.

For further information, consult the [Java App
Engine](https://developers.google.com/appengine/docs/java/overview)
documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine


