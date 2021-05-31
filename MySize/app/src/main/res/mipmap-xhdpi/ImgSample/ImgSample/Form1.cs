using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace ImgSample
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnChange_Click(object sender, EventArgs e)
        {
            Random rnd = new Random();
            int r = rnd.Next(6);

            // MessageBox.Show(r.ToString());

            switch (r)
            {
                case 0:
                    picImg.Image = ImgSample.Properties.Resources.gumi;
                    break;
                case 1:
                    picImg.Image = ImgSample.Properties.Resources._3dprinter;
                    break;
                case 2:
                    picImg.Image = ImgSample.Properties.Resources.sauce;
                    break;
                case 3:
                    picImg.Image = ImgSample.Properties.Resources.pc;
                    break;
                case 4:
                    picImg.Image = ImgSample.Properties.Resources.img;
                    break;
                case 5:
                    picImg.Image = ImgSample.Properties.Resources.banana;
                    break;

            }

        }
    }
}
