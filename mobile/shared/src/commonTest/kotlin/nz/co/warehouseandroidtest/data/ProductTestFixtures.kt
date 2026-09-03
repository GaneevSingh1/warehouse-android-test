package nz.co.warehouseandroidtest.data

internal const val PRODUCT_RESPONSE_JSON = """
{
    "product": {
        "isMaster": false,
        "onSpecial": false,
        "imageUrls": [
            "https://example.com/stool.jpg"
        ],
        "productName": "Living & Co Stacking Stool",
        "priceInfo": {
            "price": 15.0
        },
        "productKey": 2820075,
        "inventory": {
            "available": true,
            "preorderable": false,
            "backorderable": false,
            "soh": 70
        },
        "productBarcode": "9401073417602",
        "promotions": [
            {
                "promotionId": "marketclub-sandbox-5off",
                "dealDescription": "5% off your order. Download our app and join MarketClub. This is the callout",
                "demandwareConditionsText": "This is the extra detail",
                "price": 15.0,
                "isMarketClubExclusive": false,
                "description": "5% off your order. Download our app and join MarketClub",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            },
            {
                "promotionId": "easter-2024-game-sitewide-8-IncludeOnline",
                "dealDescription": "Get ${'$'}8 off when you spend ${'$'}80 on a huge range sitewide",
                "isMarketClubExclusive": false,
                "description": "Get ${'$'}8 off when you spend ${'$'}80 on a huge range sitewide",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            },
            {
                "promotionId": "easter-2024-game-sitewide-8-excludeStoreOnlineCombo",
                "dealDescription": "Get ${'$'}8 off when you spend ${'$'}80 on a huge range sitewide",
                "isMarketClubExclusive": false,
                "description": "Get ${'$'}8 off when you spend ${'$'}80 on a huge range sitewide",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            },
            {
                "promotionId": "twl-containsOnline-Promo",
                "dealDescription": "Can order online (if stock)",
                "isMarketClubExclusive": false,
                "description": "Contains Online Products Promo",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            },
            {
                "promotionId": "twl-omni-Promo",
                "dealDescription": "Omni Products",
                "isMarketClubExclusive": false,
                "description": "Omni Products Promo",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            },
            {
                "promotionId": "twl-${'$'}1app-delivery",
                "dealDescription": "${'$'}1 App Delivery on Standard Sized Products",
                "demandwareConditionsText": "Excludes OS Products",
                "isMarketClubExclusive": false,
                "description": "${'$'}1 App Delivery Standard sized products only",
                "tags": [
                    "ExcludeFromRefinement"
                ]
            }
        ],
        "brandCode": "LIV&CO",
        "brandDescription": "Living & Co",
        "imageGroups": [
            {
                "colourAttribute": "",
                "imageUrls": [
                    "https://example.com/stool-hi-res.jpg"
                ]
            }
        ],
        "productUrl": "https://www.thewarehouse.co.nz/s/twl/product/R2820075.html",
        "isDangerousGoods": false,
        "isClearance": false,
        "hasSizingChart": false,
        "compareSpecList": [],
        "clickAndCollectExcludedBranches": [
            "110",
            "192"
        ],
        "productId": "R2820075",
        "categoryId": "homegarden-furniture-dinningtableschairs-barstools",
        "categoryHierarchy": [
            {
                "categoryId": "homegarden-furniture-dinningtableschairs-barstools",
                "parentCategoryId": "homegarden-furniture-dinningtableschairs",
                "parentCategoryName": "Dining Tables & Chairs",
                "name": "Bar Stools",
                "description": "Buy Bar Stools",
                "sizeChartId": null,
                "productCount": 29,
                "subCategoryCount": 0,
                "excludeFromVisualBrowse": false
            },
            {
                "categoryId": "homegarden-furniture-dinningtableschairs",
                "parentCategoryId": "homegarden-furniture",
                "parentCategoryName": "Furniture",
                "name": "Dining Tables & Chairs",
                "description": "Buy Dining Tables",
                "sizeChartId": null,
                "productCount": 83,
                "subCategoryCount": 3,
                "excludeFromVisualBrowse": false
            },
            {
                "categoryId": "homegarden-furniture",
                "parentCategoryId": "homegarden",
                "parentCategoryName": "Home, Garden & Appliances",
                "name": "Furniture",
                "description": "Buy furniture",
                "sizeChartId": null,
                "productCount": 626,
                "subCategoryCount": 11,
                "excludeFromVisualBrowse": false
            },
            {
                "categoryId": "homegarden",
                "name": "Home, Garden & Appliances",
                "description": "Find the latest products",
                "sizeChartId": null,
                "productCount": 12512,
                "subCategoryCount": 13,
                "showInBrowse": true,
                "excludeFromVisualBrowse": false
            }
        ],
        "productDescription": "The Living &amp; Co Stacking Stool is an ideal choice for your living room.",
        "shippingSize": "Standard",
        "isOversized": false,
        "colourAttribute": "WHT",
        "colourDescription": "White",
        "soldOnline": "Y",
        "clickAndCollect": "O",
        "isClickAndCollect": true,
        "featureList": [
            "Powder coated metal legs",
            "Stackable for easy storage",
            "Maximum weight limit: 100kg"
        ],
        "isMarketPlace": false,
        "mdmProductId": "700145",
        "turnstileProtection": true
    },
    "guest": false,
    "platformDemandWare": "QAT",
    "environment": "Azure QAT",
    "developmentPlatform": true,
    "apiVersion": 4.9,
    "requestedApiVersion": 4.6
}
"""

internal const val EMPTY_PRODUCT_RESPONSE_JSON = """
{
    "guest": false
}
"""
