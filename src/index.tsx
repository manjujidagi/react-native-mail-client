import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-mail-client' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const MailClientModule = NativeModules.MailClientModule;

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

function ensureNativeModule() {
  if (!MailClientModule) {
    // Debug: log available native modules to help diagnose linking issues
    console.error(
      'MailClientModule is undefined. NativeModules available:',
      Object.keys(NativeModules)
    );
    throw new Error(
      LINKING_ERROR +
        '\nNativeModules.MailClientModule is undefined. ' +
        'Check if the native code is correctly linked and the module name matches.'
    );
  } else {
    console.log('MailClientModule is successfully linked.');
  }
}

/**
 * Connect to SMTP server
 */
export function connectSMTP(config: SMTPConfig): Promise<string> {
  ensureNativeModule();
  return MailClientModule.connectSMTP(config);
}

/**
 * Send an email via SMTP
 */
export function sendEmail(mail: Email): Promise<string> {
  ensureNativeModule();
  return MailClientModule.sendEmail(mail);
}

/**
 * Connect to IMAP server
 */
export function connectIMAP(config: IMAPConfig): Promise<string> {
  ensureNativeModule();
  return MailClientModule.connectIMAP(config);
}

/**
 * Fetch inbox mails (returns array of InboxMail)
 */
export function fetchInbox(): Promise<InboxMail[]> {
  ensureNativeModule();
  return MailClientModule.fetchInbox();
}

export default {
  connectSMTP,
  sendEmail,
  connectIMAP,
  fetchInbox,
  // Optionally export types for TypeScript users
  // SMTPConfig, IMAPConfig, Email, InboxMail
};
