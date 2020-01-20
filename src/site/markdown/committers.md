Title: Commiter Notes

## Updating the jUDDI Web Site

The site source is located under our source tree at [juddi/src/cms-site](http://svn.apache.org/repos/asf/juddi/cms-site/). 

There are two ways to edit the content for this site.

 1.  Edit the files in the Subversion repository
 1.  Using [Apache's CMS Bookmarklet](https://cms.apache.org/#bookmark)
 1.  Simply [Click here](https://cms.apache.org/redirect?uri=http://juddi.apache.org/)

Once you're done making edits, commit the changes. Changes can be previewed on the [staging site](http://juddi.staging.apache.org/). If you're satisfied with the results, then publish it to the production site.

Still have questions? Check out [this](http://www.apache.org/dev/cms.html), [this](http://www.apache.org/dev/cmsref.html) and [this](http://michelf.ca/projects/php-markdown/extra/).

When updating javadoc or xref sections, it's easier to just use svn and commit it that way

### Notes

 - Each page has a 'Title' header except for the home page.
 - Content is annotated using [Markdown](http://daringfireball.net/projects/markdown/syntax)
 - All pages, except for the home page, using this [template](http://svn.apache.org/repos/asf/juddi/cms-site/trunk/templates/skeleton.html)
 - The home page, uses this [template](http://svn.apache.org/repos/asf/juddi/cms-site/trunk/templates/skeletonHome.html)
 - When updating the navigation bars (top or left hand side), edit both templates


## Release Process

### Release Manager

One committer will be elected or hopefully volunteer to assemble the binary releases and label the source tree.

### Digitally Signing Releases

Apache policy requires binary releases be digitaly signed. The Apache process has not been formalized, but a general discussion about creating digital signatures and signing releases is available at http://nagoya.apache.org/wiki/apachewiki.cgi?SigningReleases. This covers some basics about using GnuPG to create key pairs and sign releases. Our goal here is to discuss jUDDI signing requirements, and provide some useful examples to release managers, not discuss digital signatures or encryption technology. Our discussion uses GnuPG, but any compliant software could be used. The examples below come from the GnuPG manual. This discussion is not a subsitute for reading that manual.

Creating a key pair is pretty simple using gpg. Simply invoke gpg and take all the defaults when prompted. You will have to provide a passphrase. Be sure to remember the passphrase since you'll need it anytime you use the key pair. The passphrase should itself be sufficiently secure; it shouldn't simply be a word in a dictionary, should include a mix of digits and alphanumeric characters, etc.

    gpg --gen-key
        
You should also generate a revocation certificate. This allows you to declare the key pair invalid publically, if you ever lose your private key, or it becomes compromised.

    gpg --output revoke.as --gen-revoke mykey
        
The release manager is responsible for signing the binaries. The release manager must have a public key listed in the 'KEYS' file at the root of our source tree. The release manager must create a detached signature for each binary. This detached signature must be posted along with our binaries, and allow our users to verify the binary's integrity.

    gpg --output jUDDI.tar.gzip.asc --detach-sig jUDDI.tar.gzip
        
All jUDDI committers are encouraged to create public/ private key pairs and place the public half into our 'KEYS' file at the root of our source tree. jUDDI committers are also encouraged to verify one another's keys and sign them, to help create a web of trust. Verifying a signature and a binary guarantees (in any real sense) the binary was assembled by the person that signed it. However, it does not prove the person signing it can be trusted. A web of trust can be created by signing one another's keys. This allows users and developers to 'trust' the person who created the document-signature pair to provide a secure, safe binary.

### Release Procedure

**TIP:** produce the release artifacts with JRE/JDK7

1. Ensure the build works (mvn clean install -Pdist)
2. Grab the current version's release notes using JIRA. Goto JIRA, Versions, pick the version, then Release notes. Replace the release notes html file within the source with the contents from JIRA.
3. mvn release:prepare -Papache-release
4. mvn release:perform -Papache-release
5. Sign in to [Nexus](https://repository.apache.org/) and close the staging repo.
6. Send a [VOTE] email to the dev mailing list regarding the new release.
7. Upon a successful vote, sign in to Nexus and release the staging repo
8. Add the release to svn https://dist.apache.org/repos/dist/release/juddi/
9. Update the website with updated links, update source code and user documentation
10. JIRA actions: close any tickets associated with the released version, then set the release date.
11. Promote
