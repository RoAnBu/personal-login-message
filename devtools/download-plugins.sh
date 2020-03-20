mkdir -p plugins

cd plugins

curl -o vault.jar https://media.forgecdn.net/files/2704/903/Vault.jar
curl -o essentialsx.jar https://media.forgecdn.net/files/2886/578/EssentialsX-2.17.2.0.jar
curl -o geoiptools.jar -L https://edge.forgecdn.net/files/681/222/GeoIPTools.jar

source ../copy-plm-artifact.sh