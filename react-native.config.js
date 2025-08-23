module.exports = {
  dependency: {
    platforms: {
      ios: {
        podspecPath: 'react-native-mail-client.podspec',
      },
      android: {
        packageImportPath: 'import com.mailclient.MailClientPackage;',
        packageInstance: 'new MailClientPackage()',
      },
    },
  },
};
