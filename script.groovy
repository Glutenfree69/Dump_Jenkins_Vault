/**
 * Dump credentials from folder Proxyvf - VALEURS EN CLAIR
 */
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.domains.Domain
import jenkins.model.Jenkins
import com.cloudbees.hudson.plugins.folder.Folder

def targetFolder = "ChangeMe"  // ← Ton folder

def indent = { String text, int indentationCount ->
  def replacement = "\t" * indentationCount
  text.replaceAll("(?m)^", replacement)
}

// Trouver le folder spécifique
def folder = Jenkins.get().getAllItems(Folder.class).find { it.fullName == targetFolder }

if (!folder) {
    println "❌ Folder '${targetFolder}' non trouvé!"
    println "Folders disponibles:"
    Jenkins.get().getAllItems(Folder.class).each { println "  - ${it.fullName}" }
    return
}

println "✅ Credentials pour le folder: ${targetFolder}"
println "=" * 60

CredentialsProvider.lookupStores(folder).each { store ->
    store.domains.each { domain ->
        def creds = store.getCredentials(domain)
        if (!creds.empty) {
            println indent("Domain: ${domain.name ?: 'global'}", 1)
            creds.each { cred ->
                println indent("-----------------------", 2)
                cred.properties.each { prop, val ->
                    println indent("$prop = \"$val\"", 2)
                }
            }
        }
    }
}
