import React from 'react';
import {Row, Col} from 'antd';
import FormItem from 'components/FormItem';

class Info extends React.Component {
  render() {
    return (
      <div className="products-create-tabs-inner-content">
        <Row gutter={24}>
          <Col span={15}>
            <div className="product-details-row">
              <Row gutter={24}>
                <Col span={12}>
                  <FormItem>
                    <FormItem.Title>hardware</FormItem.Title>
                    <FormItem.Content>
                      Particle Electron
                    </FormItem.Content>
                  </FormItem>
                </Col>
                <Col span={12}>
                  <FormItem>
                    <FormItem.Title>connection type</FormItem.Title>
                    <FormItem.Content>
                      GSM
                    </FormItem.Content>
                  </FormItem>
                </Col>
              </Row>
            </div>
            <div className="product-details-row">
              <Row gutter={32} className="row">
                <Col span={24}>
                  <FormItem>
                    <FormItem.Title>Description</FormItem.Title>
                    <FormItem.Content>
                      Last month, my wife, Anne Doe, took me to Las Vegas because she had to go for a business
                      convention. Needless to say, she writes for an guide to casinos and I hate gambling. But
                      then, she likes it and this supports us too, so I went along without any hassle. At first
                      I was depressed, but then as I asked around and looked around, I ended up having more fun
                      in Las Vegas than I would have thought. And no. I did not enter a single casino while I
                      was there.
                    </FormItem.Content>
                  </FormItem>
                </Col>
              </Row>
            </div>
          </Col>
          <Col span={9}>
            <div className="product-details-row product-details-image">
              <img
                src="https://store.storeimages.cdn-apple.com/4974/as-images.apple.com/is/image/AppleInc/aos/published/images/i/ph/iphonese/gallery1/iphonese-gallery1-2016?wid=835&hei=641&fmt=jpeg&qlt=95&op_sharpen=0&resMode=bicub&op_usm=0.5,0.5,0,0&iccEmbed=0&layer=comp&.v=1480454457897"
                alt=""/>
            </div>
          </Col>
        </Row>
      </div>
    );
  }
}

export default Info;
