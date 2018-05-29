# IDScanner

## IdScannerService

### Initialisatie

Om van de IdScannerService gebruik te kunnen maken, moet deze eerst geinitialiseerd worden. Dit vraagt veel rescources. Het is dus aangewezen om dit te doen in een achtergrond-thread. De initialisatie vraagt de applicatiecontext als parameter. Standaard is deze afgesteld op het lezen van de code, achteraan een identiteitskaart.

``` Java

    IdScannerService.init(applicationContext)
```

### Optioneel

Als je de IdScannerService wilt gebruiken voor andere doeleinden, is het eventueel nodig om aan de instellingen van Tesseract te sleutelen.

#### Whitelist

Het belangrijkste is de whitelist. Deze wordt standaard ingesteld op een Whitelist gefocust op identiteitskaarten. Deze whitelist zorgt ervoor dat Tesseract enkel gaat zoeken naar deze karaters in de afbeeling.

``` Java

    IdScannerService.setWhiteList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz .,")
```

#### Blacklist

Het is ook mogelijk om de blacklist aan te passen. Deze staat standaard af. De blacklist laat Tesseract zeker weten welke karakters hij niet moet vinden. Als de whitelist al is ingesteld, is dit overbodig, want hij gaat sowieso alle karaters buiten de whitelist negeren.

``` Java

    IdScannerService.setBlackList("óòô")
```

## getOcrResult

Als je gebruik maakt van een eigen cameraview of andere afbeeldingen wilt gebruiken, is het mogelijk om deze apart te verwerken met getOcrResult(). Deze methode verwacht een bitmap als argument en geeft alle tekst die hij vindt terug in een String.

``` Java

    val result: String = IdScannerService(bitmap)
```

## binarizeImage

Om de afbeeling accuraat in te lezen, is het nodig om deze om te zetten naar een afbeelding die duidelijk is. Met de methode binarizeImage(), wordt de middelwaarde van de afbeeling gezocht. Alle waardes boven de middelwaarde wordt als zwart getoond en alles er onder als wit. Dit zorgt voor een binaraire afbeelding waar de letters uitspringen.

``` Java

    val binarizedBitmap: Bitmap = binarizeBitmap(bitmap)
```

![binarizeImage](readmeImages/binarized.png)

## IdScannerView

De IdScannerView is een cameraview dat als view kan gebruikt worden. Het via deze cameraview kan er gescans worden. Met een listener kan het resultaat opgevangen en gebruikt worden.

### layout.xml

De IdScannerView kan gewoon geimpelemnteerd worden als view-element.

``` xml

<be.appwise.idscanner.views.IdScannerView
    android:id="@+id/id_scanner_camera"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

Er zijn verschillende attributen die kunnen gewijzigd worden.

|XML Attribute|Method|Values|Default Value|
|-------------|------|------|-------------|
|[`cameraSessionType`](#camerasessiontype)|`setSessionType()`|`picture` `video`|`picture`|
|[`cameraFacing`](#camerafacing)|`setFacing()`|`back` `front`|`back`|
|[`cameraFlash`](#cameraflash)|`setFlash()`|`off` `on` `auto` `torch`|`off`|
|[`cameraGrid`](#cameragrid)|`setGrid()`|`off` `draw3x3` `draw4x4` `drawPhi`|`off`|
|[`cameraCropOutput`](#cameracropoutput)|`setCropOutput()`|`true` `false`|`false`|
|[`cameraJpegQuality`](#camerajpegquality)|`setJpegQuality()`|`0 < n <= 100`|`100`|
|[`cameraVideoQuality`](#cameravideoquality)|`setVideoQuality()`|`lowest` `highest` `maxQvga` `max480p` `max720p` `max1080p` `max2160p`|`max480p`|
|[`cameraVideoCodec`](#cameravideocodec)|`setVideoCodec()`|`deviceDefault` `h263` `h264`|`deviceDefault`|
|[`cameraWhiteBalance`](#camerawhitebalance)|`setWhiteBalance()`|`auto` `incandescent` `fluorescent` `daylight` `cloudy`|`auto`|
|[`cameraHdr`](#camerahdr)|`setHdr()`|`off` `on`|`off`|
|[`cameraAudio`](#cameraaudio)|`setAudio()`|`off` `on`|`on`|
|[`cameraPlaySounds`](#cameraplaysounds)|`setPlaySounds()`|`true` `false`|`true`|
|[`cameraVideoMaxSize`](#cameravideomaxsize)|`setVideoMaxSize()`|number|`0`|
|[`cameraVideoMaxDuration`](#cameravideomaxduration)|`setVideoMaxDuration()`|number|`0`|

LET OP: De volgende attributen kunnen het gedrag van de scanner beinvloeden.

|Constraint|XML attr|SizeSelector|
|----------|--------|------------|
|min. width|`app:cameraPictureSizeMinWidth="100"`|`SizeSelectors.minWidth(100)`|
|min. height|`app:cameraPictureSizeMinHeight="100"`|`SizeSelectors.minHeight(100)`|
|max. width|`app:cameraPictureSizeMaxWidth="3000"`|`SizeSelectors.maxWidth(3000)`|
|max. height|`app:cameraPictureSizeMaxHeight="3000"`|`SizeSelectors.maxHeight(3000)`|
|min. area|`app:cameraPictureSizeMinArea="1000000"`|`SizeSelectors.minArea(1000000)`|
|max. area|`app:cameraPictureSizeMaxArea="5000000"`|`SizeSelectors.maxArea(5000000)`|
|aspect ratio|`app:cameraPictureSizeAspectRatio="1:1"`|`SizeSelectors.aspectRatio(AspectRatio.of(1,1), 0)`|
|smallest|`app:cameraPictureSizeSmallest="true"`|`SizeSelectors.smallest()`|
|biggest (**default**)|`app:cameraPictureSizeBiggest="true"`|`SizeSelectors.biggest()`|


### Gebruik

Voor veilig gebruik van de camera, moeten deze methodes in de gebruikte Activity/Fragment worden toegevoegd.

``` Java

override fun onResume() {
    super.onResume()
    id_scanner_camera.start()
}

override fun onPause() {
    super.onPause()
    id_scanner_camera.stop()
}

override fun onDestroy() {
    super.onDestroy()
    id_scanner_camera?.destroy()
}

```

#### setOnResultListener

Bij deze listener zal er één enkel resultaat worden opgehaald bij het starten van de IdScannerView. Daarna zal de IdScannerView opnieuw moeten worden gestart voor het volgende resultaat.

``` Java

id_scanner_camera.setOnResultListener(object : OnScanResultListener {
	override fun onScanResult(scan: ScanResult, bitmap: Bitmap) {
		;
	}
})

```

#### setOnContinuousResultListener

Bij deze listener zal de IdScannerView resultaten blijven doorgeven tot hij handmatig wordt stopgezet.

``` Java

id_scanner_camera.setOnContinuousResultListener(object : OnScanResultListener {
	override fun onScanResult(scan: ScanResult, bitmap: Bitmap) {
		;
	}
})

```

#### Starten en Stoppen

Het starten en stoppen van het scannen wordt met de volgende respectievelijke functies gedaan.

``` Java

id_scanner_camera.startScanning()


id_scanner_camera.stopScanning()

```
