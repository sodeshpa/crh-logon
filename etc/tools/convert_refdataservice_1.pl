# 
# This script converts all usages of the old RefDataService with the new
# RefDataGenericsService.
# It converts e.g. a call
#    getRefDataService().findOrderStatusBsEntity(Constant.ORDER_STATUS_NEW)
# into
#    getRefDataGenericsService().findBsEntity(OrderStatusBsEntity.class, Constant_ORDER_STATUS_NEW)
#
# How to run it (for example):
#    find logon-business/src -name '*.java' | xargs perl -pi etc/tools/convert_refdataservice_1.pl
#
# After running this, you should run "Organize Imports" in Eclipse over all affected files,
# because of missing import statement for the BsEntity and TxEntity classes.
# This can be done e.g. by right clicking a project in the package explorer and select
# "Source -> Organize Imports".
#
# See also the script convert_refdataservice_2.pl, which is supposed to be run _after_
# convert_refdataservice_1.pl. It takes care of places such as
#    RefDataService service = getRefDataService();
#    service.findOrderStatusBsEntity(...);
# I.e., where a call doesn't follow the pattern "getRefDataService().find...Entity".
#
# marius, 2013-03-08

# Skip special LangTx entities - these have separate findLangTx* methods in RefDataGenericsService().
if (/LangTxEntity/) {
    s/getRefDataService\(\)\.findLangTxEntity/getRefDataGenericsService().findLangTxEntity/;
    # Skip all other changes
    next;
}

# Convert findActiveFooBsEntityList(...) -> findActiveBsEntityList(Foo.class, ...)
if (/findActive([A-Za-z]+)BsEntityList/) {
    s/getRefDataService\(\).findActive([A-Za-z]+)BsEntityList\(/getRefDataGenericsService().findActiveBsEntityList(${1}BsEntity.class/;
}
# Convert findFooBsEntityList() -> findBsEntityList(Foo.class)
elsif (/find([A-Za-z]+)BsEntityList\(\)/) {
    s/getRefDataService\(\).find([A-Za-z]+)BsEntityList\(/getRefDataGenericsService().findBsEntityList(${1}BsEntity.class/;
}
# Convert findFooBsEntityList(...) -> findBsEntityList(Foo.class, ...)
else {
    s/getRefDataService\(\).find([A-Za-z]+)BsEntityList\(/getRefDataGenericsService().findBsEntityList(${1}BsEntity.class, /;
}

# Convert findFooBsEntity(...) -> findBsEntity(Foo.class, ...)
s/getRefDataService\(\).find([A-Za-z]+)BsEntity\(/getRefDataGenericsService().findBsEntity(${1}BsEntity.class, /;

# Convert findFooTxEntity(...) -> findTxEntity(Foo.class, ...)
s/getRefDataService\(\).find([A-Za-z]+)TxEntity\(/getRefDataGenericsService().findTxEntity(${1}TxEntity.class, /;

# Convert findFooTxEntityList() -> findTxEntityList(Foo.class)
if (/find[A-Za-z]+TxEntityList\(\)/) {
    s/getRefDataService\(\).find([A-Za-z]+)TxEntityList\(/getRefDataGenericsService().findTxEntityList(${1}TxEntity.class/;
}
# Convert findFooTxEntityList(...) -> findTxEntityList(Foo.class, ...)
else {
    s/getRefDataService\(\).find([A-Za-z]+)TxEntityList\(/getRefDataGenericsService().findTxEntityList(${1}TxEntity.class, /;
}

# Convert findFooTxEntityFiltered(...) -> findTxEntityFiltered(Foo.class, ...)
s/getRefDataService\(\).find([A-Za-z]+)TxEntityFiltered\(/getRefDataGenericsService().findTxEntityFiltered(${1}TxEntity.class, /;
# Convert findFooBsEntityFiltered(...) -> findBsEntityFiltered(Foo.class, ...)
s/getRefDataService\(\).find([A-Za-z]+)BsEntityFiltered\(/getRefDataGenericsService().findBsEntityFiltered(${1}BsEntity.class, /;
