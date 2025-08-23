# react-native-mail-client

Mail Client without intermediate server

## Installation

```sh
npm install react-native-mail-client
```

## Usage

```js
import { connectSMTP, sendEmail, connectIMAP, fetchInbox } from 'react-native-mail-client';

// Connect to SMTP
await connectSMTP({
  host: 'smtp.example.com',
  port: 587,
  username: 'user@example.com',
  password: 'password',
});

// Send an email
await sendEmail({
  from: 'user@example.com',
  to: 'recipient@example.com',
  subject: 'Hello',
  body: 'This is a test email.',
});

// Connect to IMAP
await connectIMAP({
  host: 'imap.example.com',
  port: 993,
  username: 'user@example.com',
  password: 'password',
});

// Fetch inbox mails
const inbox = await fetchInbox();
console.log(inbox);
```

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
