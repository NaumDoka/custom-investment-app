export const getAssetIcon = (name: string): string => {
    const icons: Record<string, string> = {
        'BTC': 'cryptocurrency-color:btc',
        'ETH': 'cryptocurrency-color:eth',
        'SOL': 'cryptocurrency-color:sol',
        'BNB': 'cryptocurrency-color:bnb',
        'XRP': 'cryptocurrency-color:xrp',
        'ADA': 'cryptocurrency-color:ada',
        'DOGE': 'cryptocurrency-color:doge',
        'DOT': 'cryptocurrency-color:dot',
        'LTC': 'cryptocurrency-color:ltc',
        'LINK': 'cryptocurrency-color:link',
        'TRX': 'cryptocurrency-color:trx',
    };
    // Fallback to a generic chart icon
    return icons[name.toUpperCase()] || 'solar:graph-up-bold-duotone';
};