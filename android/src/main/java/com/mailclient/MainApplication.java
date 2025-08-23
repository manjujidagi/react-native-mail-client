// ...existing code...
import com.mailclient.MailClientPackage;
// ...existing code...
@Override
protected List<ReactPackage> getPackages() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    List<ReactPackage> packages = new PackageList(this).getPackages();
    packages.add(new MailClientPackage());
    return packages;
}
// ...existing code...
