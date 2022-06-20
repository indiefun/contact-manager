package tech.indiefun.contactmanager;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class TelephoneStringFormatter {

    public static String format(String telephone) throws NumberParseException {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        String defaultRegionCode = GlobalSettings.getInstance().getConfigurations().getDefaultRegionCode();
        int defaultCountryCode = util.getCountryCodeForRegion(defaultRegionCode);
        Phonenumber.PhoneNumber phoneNumber = util.parse(telephone, defaultRegionCode);
        if (phoneNumber.getCountryCode() == defaultCountryCode) {
            return util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } else {
            return util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        }
    }
}
