package app.controllers;

import app.dao.PackageDao;
import app.models.Package;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

public class PackageController {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private PackageDao packageDao;

    public PackageController (PackageDao packageDao) {
       setPackageDao(packageDao);
    }

    // TODO: ADMIN CHECK
    public String createPackage() {
        Package p = new Package();
        packageDao.save(p);
        return p.getId();
    }

}
