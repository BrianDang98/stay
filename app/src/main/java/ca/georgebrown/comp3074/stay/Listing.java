package ca.georgebrown.comp3074.stay;

public class Listing {
    public String uid, time, date, title, address, description, price, numBed, numBath, userName, listingImage, listingStatus;

    public Listing(){

    }

    public Listing(String uid, String title, String price) {
        this.uid = uid;
        this.title = title;
        this.price = price;
    }

    public Listing(String uid, String title, String price, String status) {
        this.uid = uid;
        this.title = title;
        this.price = price;
        listingStatus = status;
    }

    public Listing(String uid, String time, String date, String title, String address, String description, String price, String numBed, String numBath, String userName, String listingImage, String listingStatus) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.title = title;
        this.address = address;
        this.description = description;
        this.price = price;
        this.numBed = numBed;
        this.numBath = numBath;
        this.userName = userName;
        this.listingImage = listingImage;
        this.listingStatus = listingStatus;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumBed() {
        return numBed;
    }

    public void setNumBed(String numBed) {
        this.numBed = numBed;
    }

    public String getNumBath() {
        return numBath;
    }

    public void setNumBath(String numBath) {
        this.numBath = numBath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getListingImage() {
        return listingImage;
    }

    public void setListingImage(String listingImage) {
        this.listingImage = listingImage;
    }

    public String getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(String listingStatus) {
        this.listingStatus = listingStatus;
    }
}

