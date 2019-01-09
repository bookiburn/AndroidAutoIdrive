package me.hufman.androidautoidrive

import android.content.Context
import me.hufman.idriveconnectionkit.android.CarAppResources
import me.hufman.idriveconnectionkit.android.CertMangling
import me.hufman.idriveconnectionkit.android.IDriveConnectionListener
import me.hufman.idriveconnectionkit.android.SecurityService
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStream


class CarAppAssetManager(val context: Context, val name: String): CarAppResources {
	fun loadFile(path: String): InputStream? {
		try {
			return context.assets.open(path)
		} catch (e: FileNotFoundException) {
			return null
		}
	}

	fun getAppCertificateRaw(brand: String): InputStream? {
		return loadFile("carapplications/$name/rhmi/${brand.toLowerCase()}/$name.p7b") ?:
		       loadFile("carapplications/$name/$name.p7b")
	}

	override fun getAppCertificate(brand: String): InputStream? {
		val appCert = getAppCertificateRaw(IDriveConnectionListener.brand ?: "")?.readBytes() as ByteArray
		val signedCert = CertMangling.mergeBMWCert(appCert, SecurityService.fetchBMWCerts(brandHint= IDriveConnectionListener.brand ?: ""))
		return ByteArrayInputStream(signedCert)
	}

	override fun getUiDescription(brand: String): InputStream? {
		return loadFile("carapplications/$name/rhmi/${brand.toLowerCase()}/ui_description.xml") ?:
		       loadFile("carapplications/$name/rhmi/ui_description.xml")
	}

	override fun getImagesDB(brand: String): InputStream? {
		return loadFile("carapplications/$name/rhmi/${brand.toLowerCase()}/images.zip") ?:
		loadFile("carapplications/$name/rhmi/common/images.zip")
	}

	override fun getTextsDB(brand: String): InputStream? {
		return loadFile("carapplications/$name/rhmi/${brand.toLowerCase()}/texts.zip") ?:
		       loadFile("carapplications/$name/rhmi/common/texts.zip")
	}

}