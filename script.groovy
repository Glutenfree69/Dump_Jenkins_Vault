  import com.cloudbees.plugins.credentials.CredentialsProvider
  import com.cloudbees.plugins.credentials.Credentials
  import com.cloudbees.plugins.credentials.domains.Domain
  import jenkins.model.Jenkins
  import com.cloudbees.hudson.plugins.folder.Folder
  import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl

  def targetFolder = "ChangeMe"

  def folder = Jenkins.get().getAllItems(Folder.class).find { it.fullName == targetFolder }

  if (!folder) {
      println "❌ Folder '${targetFolder}' non trouvé!"
      return
  }

  println "✅ Credentials pour le folder: ${targetFolder}"
  println "=" * 60

  CredentialsProvider.lookupStores(folder).each { store ->
      store.domains.each { domain ->
          store.getCredentials(domain).each { cred ->
              println "-----------------------"
              println "ID: ${cred.id}"
              println "Description: ${cred.description}"
              println "Class: ${cred.class.simpleName}"

              // Si c'est un FileCredentials, dump le contenu
              if (cred instanceof FileCredentialsImpl) {
                  println "FileName: ${cred.fileName}"
                  println "Content:"
                  println cred.content.text  // ← LE CONTENU EN CLAIR
              }
              println ""
          }
      }
  }
