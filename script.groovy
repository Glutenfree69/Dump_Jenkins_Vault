import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import jenkins.model.Jenkins
import org.jenkinsci.plugins.plaincredentials.impl.FileCredentialsImpl
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl

def targetItem = "folder/multipipeline" // multipepeline optionel

// Cherche n'importe quel type d'item (folder, multibranch, etc.)
def item = Jenkins.get().getItemByFullName(targetItem)

if (!item) {
    println "❌ Item '${targetItem}' non trouvé!"
    return
}

println "✅ Credentials pour: ${targetItem}"
println "Type: ${item.class.simpleName}"
println "=" * 60

CredentialsProvider.lookupStores(item).each { store ->
    store.domains.each { domain ->
        store.getCredentials(domain).each { cred ->
            println "-----------------------"
            println "ID: ${cred.id}"
            println "Description: ${cred.description}"
            println "Class: ${cred.class.simpleName}"
            
            if (cred instanceof FileCredentialsImpl) {
                println "FileName: ${cred.fileName}"
                println "Content:\n${cred.content.text}"
            }
            else if (cred instanceof StringCredentialsImpl) {
                println "Secret: ${cred.secret.plainText}"
            }
            else if (cred instanceof UsernamePasswordCredentialsImpl) {
                println "Username: ${cred.username}"
                println "Password: ${cred.password.plainText}"
            }
            else if (cred instanceof BasicSSHUserPrivateKey) {
                println "Username: ${cred.username}"
                println "Passphrase: ${cred.passphrase?.plainText ?: '(none)'}"
                println "PrivateKey:\n${cred.privateKey}"
            }
            else if (cred instanceof CertificateCredentialsImpl) {
                println "Password: ${cred.password.plainText}"
            }
            else {
                cred.properties.each { prop, val ->
                    if (val?.toString()?.contains('Secret')) {
                        try {
                            println "${prop}: ${val.plainText}"
                        } catch (e) {
                            println "${prop}: ${val}"
                        }
                    } else {
                        println "${prop}: ${val}"
                    }
                }
            }
            println ""
        }
    }
}
