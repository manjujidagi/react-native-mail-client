import Foundation
import MailCore

@objc(MailClientModule)
class MailClientModule: NSObject {
    var imapConfig: [String: Any] = [:]
    var smtpConfig: [String: Any] = [:]

    @objc(connectIMAP:resolver:rejecter:)
    func connectIMAP(config: NSDictionary, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        imapConfig = config as? [String: Any] ?? [:]
        resolver("IMAP Connected")
    }

    @objc(connectSMTP:resolver:rejecter:)
    func connectSMTP(config: NSDictionary, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        smtpConfig = config as? [String: Any] ?? [:]
        resolver("SMTP Connected")
    }

    @objc(fetchInbox:rejecter:)
    func fetchInbox(resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        guard let host = imapConfig["host"] as? String,
              let port = imapConfig["port"] as? Int,
              let username = imapConfig["username"] as? String,
              let password = imapConfig["password"] as? String else {
            rejecter("IMAP_CONFIG_ERROR", "IMAP config missing", nil)
            return
        }

        let session = MCOIMAPSession()
        session.hostname = host
        session.port = UInt32(port)
        session.username = username
        session.password = password
        session.connectionType = .TLS

        let requestKind: MCOIMAPMessagesRequestKind = [.headers, .structure, .internalDate, .headerSubject, .flags]
        let inboxFolder = "INBOX"
        let fetchOp = session.fetchMessagesOperation(withFolder: inboxFolder, requestKind: requestKind, uids: MCOIndexSet(range: MCORange(location: 1, length: UInt64.max)))

        fetchOp?.start { error, messages, vanished in
            if let error = error {
                rejecter("IMAP_ERROR", error.localizedDescription, error)
                return
            }
            var mails: [[String: Any]] = []
            let lastMessages = (messages as? [MCOIMAPMessage])?.suffix(20) ?? []
            for msg in lastMessages.reversed() {
                let subject = msg.header.subject ?? ""
                let from = msg.header.from?.mailbox ?? ""
                let date = msg.header.date?.description ?? ""
                let snippet = "" // For brevity, not fetching body here
                mails.append([
                    "subject": subject,
                    "from": from,
                    "date": date,
                    "snippet": snippet
                ])
            }
            resolver(mails)
        }
    }

    @objc(sendEmail:resolver:rejecter:)
    func sendEmail(mail: NSDictionary, resolver: @escaping RCTPromiseResolveBlock, rejecter: @escaping RCTPromiseRejectBlock) {
        guard let host = smtpConfig["host"] as? String,
              let port = smtpConfig["port"] as? Int,
              let username = smtpConfig["username"] as? String,
              let password = smtpConfig["password"] as? String,
              let from = mail["from"] as? String,
              let to = mail["to"] as? String,
              let subject = mail["subject"] as? String,
              let body = mail["body"] as? String else {
            rejecter("SMTP_CONFIG_ERROR", "SMTP config or mail missing", nil)
            return
        }

        let smtpSession = MCOSMTPSession()
        smtpSession.hostname = host
        smtpSession.port = UInt32(port)
        smtpSession.username = username
        smtpSession.password = password
        smtpSession.connectionType = .TLS

        let builder = MCOMessageBuilder()
        builder.header.from = MCOAddress(mailbox: from)
        builder.header.to = [MCOAddress(mailbox: to)]
        builder.header.subject = subject
        builder.htmlBody = body

        let rfc822Data = builder.data()
        let sendOperation = smtpSession.sendOperation(with: rfc822Data)
        sendOperation?.start { error in
            if let error = error {
                rejecter("SMTP_ERROR", error.localizedDescription, error)
            } else {
                resolver("Email sent")
            }
        }
    }
}
