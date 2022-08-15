source .github/properties.sh
CURRENT_TZDB=$(getProperty 'ticktock.ianaVersion' gradle.properties)
echo "current: $CURRENT_TZDB"
echo "CURRENT_TZDB=$CURRENT_TZDB"
wget -q https://data.iana.org/time-zones/tzdata-latest.tar.gz
tar -xf tzdata-latest.tar.gz version
LATEST_TZDB=$(cat version)
echo "latest: $LATEST_TZDB"
echo "LATEST_TZDB=$LATEST_TZDB"
echo "current: $CURRENT_TZDB"
echo "latest:  $LATEST_TZDB"
echo "force refreshing to:  $LATEST_TZDB"
sed -i -e "s/${CURRENT_TZDB}/${LATEST_TZDB}/g" gradle.properties
rm version
rm tzdata-latest.tar.gz
mv gradle.properties-e gradle.properties
./regenerateData.sh
./gradlew check
