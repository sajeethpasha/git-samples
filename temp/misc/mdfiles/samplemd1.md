### Endpoint Details for Unit of Measure Conversion

The endpoint being utilized is:

`/{item-id}/conversion/{uom-code}/gallons`

This endpoint is used to perform conversions between units of measure, specifically for converting from kilograms to liters. While it has been suggested to rename this API, such a change would not yield any performance improvement. Therefore, it is recommended to retain the current naming convention.

Currently, the PMX Backend-for-Frontend (BFF) implementation is not aligned with BAMA BFF, but changes are in progress to address this. Furthermore, an alternative endpoint has been identified for conversions from kilograms to liters, as follows:

`/conversion/{item-id}/LT/{uom-code}`

This alternative endpoint provides a conversion to liters, which fulfills the current requirement. It is advisable to consider whether this endpoint can be leveraged to meet the necessary conversion requirements.

Both of these conversion endpoints invoke a domain API to retrieve the `interclassConversion` values. The relevant domain API endpoint is:

```
https://dev-wbm-inv.np-edps-aks.shwaks.com/api/v1/inventory/unit-of-measure-classes/WEIGHT/conversions/inter?inventoryItemId=100003357085427
```

#### Sample Request and Response

**Request:**
```bash
curl -X GET "https://dev-wbm-inv.np-edps-aks.shwaks.com/api/v1/inventory/unit-of-measure-classes/WEIGHT/conversions/inter?inventoryItemId=100003357085427&toUOMCode=LT&fromUOMCode=KG" -H "Accept: application/json"
```

**Response:**
```json
{
    "items": [
        {
            "interclassConversionId": 300002483821336,
            "inventoryItemId": 100003357085427,
            "fromUOMClassId": "9",
            "fromUOMCode": "KG",
            "toUOMClassId": "8",
            "toUOMCode": "LT",
            "interclassConversion": 0.94,
            "interclassConversionEndDate": null,
            "itemNumber": "395485",
            "createdBy": "XXSW_CONVERSION",
            "creationDate": "2024-02-23",
            "lastUpdatedBy": "erpcloudintscmuser",
            "lastUpdateDate": "2024-07-03"
        }
    ],
    "count": 1,
    "hasMore": false,
    "limit": 25,
    "offset": 0,
    "totalResults": 1
}
```

This response contains all the relevant details for the conversion, including conversion factors, metadata, and the inventory item details.

