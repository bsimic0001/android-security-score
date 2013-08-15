package com.device.security.analytics.androidsecurityanalyticspro.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.device.security.analytics.androidsecurityanalyticspro.beans.AppDetailBean;
import com.device.security.analytics.androidsecurityanalyticspro.beans.PermBean;
import com.device.security.analytics.androidsecurityanalyticspro.utils.AnalyticsUtils;
import com.device.security.analytics.androidsecurityanalyticspro.utils.AppDateUtil;
import com.device.security.analytics.androidsecurityanalyticspro.utils.LockPatternUtils;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.stericson.RootTools.RootTools;

public class FileCreationHelper {

	public static String FILE_NAME_PREFIX = "DeviceSecurityAnalyticsResults";
	public static String FILE_NAME_EXT = ".pdf";

	private static Font catFont = new Font(Font.FontFamily.HELVETICA, 18,
			Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.HELVETICA, 12,
			Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.HELVETICA, 16,
			Font.BOLD);
	private static Font mediumBold = new Font(Font.FontFamily.HELVETICA, 14,
			Font.BOLD);

	private static Font smallBold = new Font(Font.FontFamily.HELVETICA, 12,
			Font.BOLD);
	
	private static Font smallBoldRed = new Font(Font.FontFamily.HELVETICA, 12,
			Font.BOLD, BaseColor.RED);

	private static Font smallItalic = new Font(Font.FontFamily.HELVETICA, 12,
			Font.ITALIC);

	private static Font mediumBoldBlue = new Font(Font.FontFamily.HELVETICA,
			14, Font.BOLD, BaseColor.BLUE);

	public static File createFile(PackageManager packageManager,
			LockPatternUtils lockUtils, Context context) {
		File file = null;

		String status = Environment.getExternalStorageState();

		File root;

		if (status.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
			root = Environment.getExternalStorageDirectory();
		} else {
			root = context.getFilesDir();
		}

		File dir = new File(root.getAbsolutePath() + "/AndroidAnalyticsData");
		dir.mkdirs();

		Time now = new Time();
		now.setToNow();

		String timeString = now.format2445();

		file = new File(dir, FILE_NAME_PREFIX + timeString + FILE_NAME_EXT);
		FileOutputStream out = null;
		try {

			ArrayList<AppDetailBean> allAppsList = new ArrayList<AppDetailBean>();
			ArrayList<AppDetailBean> criticalAppsList = new ArrayList<AppDetailBean>();
			ArrayList<AppDetailBean> highAppsList = new ArrayList<AppDetailBean>();
			ArrayList<AppDetailBean> mediumAppsList = new ArrayList<AppDetailBean>();
			ArrayList<AppDetailBean> lowAppsList = new ArrayList<AppDetailBean>();

			ArrayList<AppDetailBean> appDetailList;
			appDetailList = AppResultsHelper.calculateAppsList(packageManager);

			AppResultsHelper
					.calculateAppResults(appDetailList, allAppsList,
							criticalAppsList, highAppsList, mediumAppsList,
							lowAppsList);

			out = new FileOutputStream(file);
			Document document = new Document();

			PdfWriter.getInstance(document, out);
			document.open();
			addMetaData(document);
			addTitlePage(document, packageManager, lockUtils, allAppsList,
					criticalAppsList, highAppsList, mediumAppsList, lowAppsList);
			addContent(document, packageManager, context, lockUtils, allAppsList,
					criticalAppsList, highAppsList, mediumAppsList, lowAppsList);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;

	}

	private static void addMetaData(Document document) {
		document.addTitle("Android Device Security Score Result");
		document.addSubject("Developed by Simic Software LLC. To improve security awareness for all!");
		document.addKeywords("Android, Security, Android Security, Device Security, Mobile, Mobile Security, Analytics, Permissions, Protecting Data");
		document.addAuthor("Android Security Analytics App");
		document.addCreator("Simic Software LLC");
	}

	private static void addTitlePage(Document document,
			PackageManager packageManager, LockPatternUtils lockUtils,
			ArrayList<AppDetailBean> allAppsList,
			ArrayList<AppDetailBean> criticalAppsList,
			ArrayList<AppDetailBean> highAppsList,
			ArrayList<AppDetailBean> mediumAppsList,
			ArrayList<AppDetailBean> lowAppsList) throws DocumentException {
		Paragraph preface = new Paragraph();
		// We add one empty line
		addEmptyLine(preface, 1);
		// Lets write a big header
		preface.add(new Paragraph("Android Device Security Score Results",
				catFont));

		Paragraph serial = new Paragraph(
				"Device Build Serial #" + Build.SERIAL, mediumBoldBlue);

		
		preface.add(serial);
		
		addEmptyLine(preface, 1);
		// Will create: Report generated by: _name, _date
		preface.add(new Paragraph("Report generated by: "
				+ "Android Security Analytics App" + ", " + new Date(),
				smallBold));
		addEmptyLine(preface, 1);
		preface.add(new Paragraph(
				"This document contains the results generated by the Android Security Analytics App. "
						+ "The results will outline the security of applications installed on this device as "
						+ "well as the devices security posture with regard to security settings currently in use."));

		addEmptyLine(preface, 2);

		addOverallResults(document, preface, packageManager, lockUtils,
				allAppsList, criticalAppsList, highAppsList, mediumAppsList,
				lowAppsList);
		addEmptyLine(preface, 1);

		document.add(preface);
		// Start a new page
		// document.newPage();
	}

	private static void addOverallResults(Document document, Paragraph preface,
			PackageManager packageManager, LockPatternUtils lockUtils,
			ArrayList<AppDetailBean> allAppsList,
			ArrayList<AppDetailBean> criticalAppsList,
			ArrayList<AppDetailBean> highAppsList,
			ArrayList<AppDetailBean> mediumAppsList,
			ArrayList<AppDetailBean> lowAppsList) {

		int totalScore = AppResultsHelper.calculateRisk(packageManager,
				lockUtils);

		Paragraph overallScore = new Paragraph("Your Device's Total Score is "
				+ totalScore + "/100", catFont);
		preface.add(overallScore);
		addEmptyLine(preface, 1);

		Paragraph totalApps = new Paragraph("Total number of apps analyzed: "
				+ allAppsList.size());
		preface.add(totalApps);
		addEmptyLine(preface, 0);

		Paragraph criticalApps = new Paragraph(
				"Apps with critical likelihood for malware: "
						+ criticalAppsList.size());
		preface.add(criticalApps);
		addEmptyLine(preface, 0);

		Paragraph highApps = new Paragraph(
				"Apps with high likelihood for malware: "
						+ highAppsList.size());
		preface.add(highApps);
		addEmptyLine(preface, 0);

		Paragraph mediumApps = new Paragraph(
				"Apps with medium likelihood for malware: "
						+ mediumAppsList.size());
		preface.add(mediumApps);
		addEmptyLine(preface, 0);

		Paragraph lowApps = new Paragraph(
				"Apps low likelihood for malware: "
						+ lowAppsList.size());
		preface.add(lowApps);
		addEmptyLine(preface, 0);

		addEmptyLine(preface, 2);
		Paragraph resultInfo = new Paragraph("How are my results calculated?",
				subFont);
		preface.add(resultInfo);
		addEmptyLine(preface, 1);

		Paragraph resultExplanation = new Paragraph(
				"Results are calculated by analyzing several key factors that contribute to malware on Android devices. "
						+ "The first and most influential is the permissions that your apps have on your device. Using the results of multiple studies relating to Malware in "
						+ "Android devices, we analyze the permissions your apps request and compare them to the most commonly used permissions by malware. "
						+ "Depending on which permissions an application requests, we use data collected by these studies to calculate a potential risk of using that app. "
						+ "For example, an app that is a simple puzzle game should not have permissions to read all your contacts and email them.");

		addEmptyLine(resultExplanation, 1);

		resultExplanation
				.add(new Paragraph(
						"Other factors that contribute to your overall score are the security settings currently enabled on your device and the "
								+ "age of your apps. Not using a secure locking mechanism on your device can allow easy access in case it is lost or stolen. "
								+ "Studies have also shown that apps that are not regularly updated are much more likely to contain security vulnerabilities."));

		preface.add(resultExplanation);
		addEmptyLine(preface, 1);

		Paragraph improveInfo = new Paragraph("How can I improve my score?",
				subFont);
		preface.add(improveInfo);
		addEmptyLine(preface, 1);

		Paragraph improveExplanation = new Paragraph(
				"The simplest way to improve your score is to remove potentially dangerous apps. "
						+ "This can be done by using the Android Security Analytics app to view your " +
						"high risk apps and then remove them. " +
						"Also, make sure to remove any third party apps. "
						+ "Third party apps are ones that are installed on your Android device that do not " +
						"come from an offical source such as the Google Play Store. "
						+ "Studies have shown that third party apps are much more likely to contain malware."
						+ "You should also always make sure you're using a secure locking mechanism. " +
						"Make use of a strong password or PIN that is at least 6 characters long."
						+ "Finally, you should make sure that all your apps are up to date by " +
						"downloading updates from the Google Play Store.");

		preface.add(improveExplanation);
		addEmptyLine(preface, 1);

	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private static void addContent(Document document,
			PackageManager packageManager, Context context, LockPatternUtils lockUtils,
			ArrayList<AppDetailBean> allAppsList,
			ArrayList<AppDetailBean> criticalAppsList,
			ArrayList<AppDetailBean> highAppsList,
			ArrayList<AppDetailBean> mediumAppsList,
			ArrayList<AppDetailBean> lowAppsList) throws DocumentException {

		Anchor anchor = new Anchor(
				"Apps Installed and Their Likelihood to Contain Malware",
				catFont);
		anchor.setName("Apps Installed and Their Likelihood to Contain Malware");

		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		Paragraph chapter1Info = new Paragraph(
				"This section will show all the apps installed on this device "
						+ "and order them by their likelihood to contain malware. "
						+ "These results are based off of research done on the permissions " +
						"requested by malware as well as other factors "
						+ "such as age and whether or not the app comes from a third party.");

		catPart.add(chapter1Info);
		catPart.add(new Paragraph("   "));

		Paragraph criticalAppsParagraph = new Paragraph(
				"Apps with critical likelihood of malware", mediumBoldBlue);
		addEmptyLine(criticalAppsParagraph, 1);
		addAppsToParagraph(criticalAppsList, criticalAppsParagraph,
				packageManager, context);
		catPart.add(criticalAppsParagraph);

		Paragraph highAppsParagraph = new Paragraph(
				"Apps with high likelihood of malware", mediumBoldBlue);
		addEmptyLine(highAppsParagraph, 1);
		addAppsToParagraph(highAppsList, highAppsParagraph, packageManager, context);
		catPart.add(highAppsParagraph);

		Paragraph mediumAppsParagraph = new Paragraph(
				"Apps with medium likelihood of malware", mediumBoldBlue);
		addEmptyLine(mediumAppsParagraph, 1);
		addAppsToParagraph(mediumAppsList, mediumAppsParagraph, packageManager, context);
		catPart.add(mediumAppsParagraph);

		Paragraph lowAppsParagraph = new Paragraph(
				"Apps with low likelihood of malware", mediumBoldBlue);
		addEmptyLine(lowAppsParagraph, 1);
		addAppsToParagraph(lowAppsList, lowAppsParagraph, packageManager, context);
		catPart.add(lowAppsParagraph);

		document.add(catPart);

		// ---------------CHAPTER 2 - Device Security
		// -------------------------------

		// Next section
		anchor = new Anchor("Device Security Settings Analysis", catFont);
		anchor.setName("Device Security Settings Analysis");

		// Second parameter is the number of the chapter
		catPart = new Chapter(new Paragraph(anchor), 2);
		catPart.add(new Paragraph(
				"This section will discuss the current security settings enabled on this device "
						+ "in terms of the locking mechanism and data encryption."));
		catPart.add(new Paragraph("   "));

		Paragraph deviceParagraph = new Paragraph();

		addDeviceSecurityToParagraph(packageManager, deviceParagraph, lockUtils);
		catPart.add(deviceParagraph);

		// Now add all this to the document
		document.add(catPart);

	}

	private static void addDeviceSecurityToParagraph(
			PackageManager packageManager, Paragraph deviceParagraph,
			LockPatternUtils lockUtils) {

		Paragraph lockHeader = new Paragraph("Lock Settings on Android Device",
				mediumBoldBlue);

		Paragraph lockAnalysis;

		if (lockUtils.isLockPasswordEnabled()) {
			lockAnalysis = new Paragraph(
					"This device is using a password or pin lock setting. "
							+ ". It is recommended to use a password or PIN of at least 6 characters. It is also important to make this value complex and not easily guessable. " +
							"Values such as anniversaries, birthdays, and pet names are not recommended. Remember, all the data on your device is protected by this value.");
		} else if (lockUtils.isLockPatternEnabled()) {
			lockAnalysis = new Paragraph(
					"This device requires a lock pattern to unlock the device. "
							+ "While this is better than no lock mechanism, it is easily discoverable and guessable. "
							+ "It is recommended to use a password or PIN of at least 6 characters.");
		} else {
			lockAnalysis = new Paragraph(
					"This device has no lock mechanism enabled. "
							+ "This is considered very unsafe since anyone with access to this device can view app data on it. "
							+ "It is recommended to use a password or PIN of at least 6 characters.");
		}

		lockHeader.add(lockAnalysis);
		deviceParagraph.add(lockHeader);

		addEmptyLine(deviceParagraph, 1);

		Paragraph encryptionHeader = new Paragraph(
				"Device Encryption Settings", mediumBoldBlue);

		Paragraph encryptionPara;
		if (lockUtils.getEncryptionScheme()) {
			encryptionPara = new Paragraph(
					"We have detected that this device is using encryption "
							+ "to encrypt all data on the device. This is the preferred setting.");
		} else {
			encryptionPara = new Paragraph(
					"We have detected that this device is NOT using encryption. "
							+ "Encryption is strongly recommended since it will make your data unusable to an "
							+ "attacker in the event of a device compromise via malware or theft.");
		}

		encryptionHeader.add(encryptionPara);
		deviceParagraph.add(encryptionHeader);
		
		
		addEmptyLine(deviceParagraph, 1);
		Paragraph rootHeader = new Paragraph("Device Root Status", mediumBoldBlue);
		
		Paragraph rootPara;
		if(RootTools.isRootAvailable()){
			rootPara = new Paragraph("We have detected that this device is rooted. " +
					"Rooting a device gives you complete control of your device. " +
					"However, it also gives potential malware root access to your device as well, which is not good. " +
					"You should not root your phone unless it's absolutely necessary. " +
					"If you do root it, you should make sure to undo that once you're finished doing what you need it for. ");
		}
		else{
			//not rooted
			rootPara = new Paragraph("We have detected that this device is not rooted. This is the optimal setting for security.");
		}
		
		rootHeader.add(rootPara);
		deviceParagraph.add(rootHeader);

	}

	private static void addAppsToParagraph(ArrayList<AppDetailBean> appsList,
			Paragraph paragraph, PackageManager packageManager, Context context) {
		int counter = 1;
		for (Iterator iterator = appsList.iterator(); iterator.hasNext();) {
			AppDetailBean bean = (AppDetailBean) iterator.next();

			Paragraph p;
			
			String thirdPartyString = "";
			if (packageManager.getInstallerPackageName(bean.getPackageName()) == null) {
				thirdPartyString = " - Third Party App!";
				p = new Paragraph(counter + ". " + bean.getAppName()
						+ thirdPartyString, smallBoldRed);
			} else {
				p = new Paragraph(counter + ". " + bean.getAppName()
						+ thirdPartyString, smallBold);
			}

			int monthsSinceUpdate = AppDateUtil.getMonthsSinceUpdate(
					packageManager, bean.getPackageName());
			int top30Perms = bean.getPermissionBean().getTop30Permissions()
					+ bean.getPermissionBean().getTop20Permissions()
					+ bean.getPermissionBean().getTop10Permissions();

			Paragraph details = new Paragraph("    " + monthsSinceUpdate
					+ " Month(s) since update.");
			p.add(details);

			Paragraph detailsTwo = new Paragraph("    " + "Contains "
					+ top30Perms
					+ "/30 of the most requested permissions by malware.");
			p.add(detailsTwo);

			Paragraph detailsThree = new Paragraph("    Permissions:",
					smallItalic);
			p.add(detailsThree);

			String[] perms = bean.getPermissionBean().getPermissions();

			
			DatabaseHelper dbHelper = new DatabaseHelper(
					context);

			try {
				dbHelper.createDataBase();
			} catch (IOException e) {
				//Log.d("ERROR", e.getMessage(), e);
			}
			dbHelper.openDataBase();

			ArrayList<PermBean> permBeans = dbHelper.getPerms();
			
			for (int i = 0; i < perms.length; i++) {
				
				PermBean pb = AnalyticsUtils.getMatchingPermBean(permBeans, perms[i]);
				Paragraph permParagraph;
				if(pb != null)
					 permParagraph = new Paragraph("         " + pb.getDisplayName());
				else
					permParagraph = new Paragraph("         " + perms[i].substring(perms[i].lastIndexOf(".") + 1, perms[i].length()));
				p.add(permParagraph);
			}

			paragraph.add(p);
			counter++;
		}
	}

	private static void createTable(Section subCatPart)
			throws BadElementException {
		PdfPTable table = new PdfPTable(3);

		// t.setBorderColor(BaseColor.GRAY);
		// t.setPadding(4);
		// t.setSpacing(4);
		// t.setBorderWidth(1);

		PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Table Header 2"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase("Table Header 3"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);

		table.addCell("1.0");
		table.addCell("1.1");
		table.addCell("1.2");
		table.addCell("2.1");
		table.addCell("2.2");
		table.addCell("2.3");

		subCatPart.add(table);

	}

	private static void createList(Section subCatPart) {
		List list = new List(true, false, 10);
		list.add(new ListItem("First point"));
		list.add(new ListItem("Second point"));
		list.add(new ListItem("Third point"));
		subCatPart.add(list);
	}

}
