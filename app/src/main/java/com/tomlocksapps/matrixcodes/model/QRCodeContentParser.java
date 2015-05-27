package com.tomlocksapps.matrixcodes.model;

import org.opencv.core.Point;

import java.util.List;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class QRCodeContentParser {

    public static QRCodeContent parseCode(String content, List<Integer> dividers) {
        QRCodeContent qrCodeContent = new QRCodeContent();

        try {
            qrCodeContent.setSize(Integer.valueOf(content.substring(0, dividers.get(0))));
            qrCodeContent.setPoint(new Point(Double.valueOf(content.substring(dividers.get(0), dividers.get(1))), Double.valueOf(content.substring(dividers.get(1), dividers.get(2)))));
            qrCodeContent.setAngle(Integer.valueOf(content.substring(dividers.get(2), dividers.get(3))));
            qrCodeContent.setZ(Integer.valueOf(content.substring(dividers.get(3), dividers.get(4))));
        } catch(NumberFormatException e) {
            return null;
        } catch(IndexOutOfBoundsException e) {
            return null;
        }

        return qrCodeContent;
    }
}
