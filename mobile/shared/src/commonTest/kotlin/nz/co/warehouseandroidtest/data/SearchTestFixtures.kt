package nz.co.warehouseandroidtest.data

internal const val SEARCH_RESPONSE_JSON = """
{
    "products": [
        {
            "productName": "Living & Co Stacking Stool",
            "productKey": 2820075,
            "priceInfo": {
                "price": 15.0
            },
            "imageGroups": [
                {
                    "colourAttribute": "",
                    "imageUrls": [
                        "https://example.com/stool-group.jpg"
                    ]
                }
            ],
            "productImageUrl": "https://example.com/stool.jpg",
            "productId": "R2820075",
            "brandDescription": "Living & Co"
        },
        {
            "productName": "Image Group Stool",
            "productKey": 111,
            "productId": "R111",
            "priceInfo": {
                "price": 20.5
            },
            "imageGroups": [
                {
                    "imageUrls": [
                        "https://example.com/group-only.jpg"
                    ]
                }
            ],
            "brandDescription": "House"
        },
        {
            "productName": "No Image Stool",
            "productKey": 222,
            "productId": "R222",
            "priceInfo": {
                "price": 9.99
            }
        }
    ],
    "searchTerm": "stool",
    "total": 64
}
"""

internal const val EMPTY_SEARCH_RESPONSE_JSON = """
{
    "products": [],
    "searchTerm": "xyz",
    "total": 0
}
"""
