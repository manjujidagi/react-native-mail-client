// ...existing code...
import com.mailclient.MailClientPackage;
import com.facebook.react.PackageList; // <-- Add this import if missing
// ...existing code...
@Override
protected List<ReactPackage> getPackages() {
    @SuppressWarnings("UnnecessaryLocalVariable")
    List<ReactPackage> packages = new PackageList(this).getPackages();
    packages.add(new MailClientPackage());
    return packages;
}
// ...existing code...
