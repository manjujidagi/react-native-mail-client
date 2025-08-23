import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-mail-client' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const MailClientModule = NativeModules.MailClientModule
  ? NativeModules.MailClientModule
  : new Proxy({}, {
      get() {
        throw new Error(LINKING_ERROR);
      },
    });

export interface SMTPConfig {
  host: string;
  port: number;
  username: string;
  password: string;
}

export interface IMAPConfig {
  host: string;
  port: number;
  username: string;
  password: string;
}

export interface Email {
  from: string;
  to: string;
  subject: string;
  body: string;
}

export interface InboxMail {
  subject: string;
  from: string;
  date: string;
  snippet: string;
}

/**
 * Connect to SMTP server
 */
export function connectSMTP(config: SMTPConfig): Promise<string> {
  return MailClientModule.connectSMTP(config);
}

/**
 * Send an email via SMTP
 */
export function sendEmail(mail: Email): Promise<string> {
  return MailClientModule.sendEmail(mail);
}

/**
 * Connect to IMAP server
 */
export function connectIMAP(config: IMAPConfig): Promise<string> {
  return MailClientModule.connectIMAP(config);
}

/**
 * Fetch inbox mails (returns array of InboxMail)
 */
export function fetchInbox(): Promise<InboxMail[]> {
  return MailClientModule.fetchInbox();
}
