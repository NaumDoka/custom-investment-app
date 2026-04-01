export interface Country {
    name: string;
    code: string;
    prefix: string;
    flag: string;
}

export const countries: Country[] = [
    { name: 'United States', code: 'US', prefix: '+1', flag: 'twemoji:flag-united-states' },
    { name: 'United Kingdom', code: 'GB', prefix: '+44', flag: 'twemoji:flag-united-kingdom' },
    { name: 'Albania', code: 'AL', prefix: '+355', flag: 'twemoji:flag-albania' },
    { name: 'Australia', code: 'AU', prefix: '+61', flag: 'twemoji:flag-australia' },
    { name: 'Austria', code: 'AT', prefix: '+43', flag: 'twemoji:flag-austria' },
    { name: 'Belgium', code: 'BE', prefix: '+32', flag: 'twemoji:flag-belgium' },
    { name: 'Canada', code: 'CA', prefix: '+1', flag: 'twemoji:flag-canada' },
    { name: 'China', code: 'CN', prefix: '+86', flag: 'twemoji:flag-china' },
    { name: 'Croatia', code: 'HR', prefix: '+385', flag: 'twemoji:flag-croatia' },
    { name: 'France', code: 'FR', prefix: '+33', flag: 'twemoji:flag-france' },
    { name: 'Germany', code: 'DE', prefix: '+49', flag: 'twemoji:flag-germany' },
    { name: 'Greece', code: 'GR', prefix: '+30', flag: 'twemoji:flag-greece' },
    { name: 'India', code: 'IN', prefix: '+91', flag: 'twemoji:flag-india' },
    { name: 'Italy', code: 'IT', prefix: '+39', flag: 'twemoji:flag-italy' },
    { name: 'Japan', code: 'JP', prefix: '+81', flag: 'twemoji:flag-japan' },
    { name: 'Kosovo', code: 'XK', prefix: '+383', flag: 'twemoji:flag-kosovo' },
    { name: 'Montenegro', code: 'ME', prefix: '+382', flag: 'twemoji:flag-montenegro' },
    { name: 'Netherlands', code: 'NL', prefix: '+31', flag: 'twemoji:flag-netherlands' },
    { name: 'North Macedonia', code: 'MK', prefix: '+389', flag: 'twemoji:flag-north-macedonia' },
    { name: 'Portugal', code: 'PT', prefix: '+351', flag: 'twemoji:flag-portugal' },
    { name: 'Spain', code: 'ES', prefix: '+34', flag: 'twemoji:flag-spain' },
    { name: 'Sweden', code: 'SE', prefix: '+46', flag: 'twemoji:flag-sweden' },
    { name: 'Switzerland', code: 'CH', prefix: '+41', flag: 'twemoji:flag-switzerland' },
    { name: 'Turkey', code: 'TR', prefix: '+90', flag: 'twemoji:flag-turkey' },
    { name: 'UAE', code: 'AE', prefix: '+971', flag: 'twemoji:flag-united-arab-emirates' },
];